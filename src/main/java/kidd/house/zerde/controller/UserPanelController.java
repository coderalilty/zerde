package kidd.house.zerde.controller;

import kidd.house.zerde.dto.user.*;
import kidd.house.zerde.service.KaspiService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserPanelController {
    private final UserService userService;
    private final KaspiService kaspiService;
    @GetMapping("/profile/{user_id}")
    public ResponseEntity<UserProfileDto> getUserProfiles(@PathVariable int user_id) {
        UserProfileDto userProfileDto = userService.getUserProfiles(user_id);
        return ResponseEntity.ok(userProfileDto);
    }
/*    @PutMapping("/edit_user/{teacher_id}")
    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody EditTeacherDto editTeacherDto){
        userService.editTeacher(teacher_id,editTeacherDto);
        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
   }*/
    @PutMapping("/edit_user/{user_id}")
    public ResponseEntity<String> editUser(@PathVariable int user_id,
                                           @RequestBody EditUserDto editUserDto) {
        userService.editUser(user_id, editUserDto);
    return new ResponseEntity<>("User edited", HttpStatus.OK);

    }
    @GetMapping("/subscriptions/{user_id}")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable int user_id) {
        return userService.getUserSubscriptions(user_id);
    }
    @PostMapping("/buy-subscription")
    public ResponseEntity<KaspiPaymentResponseDto> buySubscription(@RequestBody PurchaseSubscriptionDto dto) {
        KaspiPaymentResponseDto resp = userService.purchase(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/trial_lesson/{user_id}")
    public ResponseEntity<List<LessonDto>> getTrialLessons(@PathVariable int user_id) {
        List<LessonDto> trialLessons = userService.getTrialLessons(user_id);
        return ResponseEntity.ok(trialLessons);
    }

    @GetMapping("/permanent_lessons/{user_id}")
    public ResponseEntity<List<LessonDto>> getPermanentLessons(@PathVariable int user_id) {
        List<LessonDto> permanentLessons = userService.getPermanentLessons(user_id);
        return ResponseEntity.ok(permanentLessons);
    }
    @PostMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestBody KaspiCallbackDto dto,
            @RequestHeader(value = "X-Kaspi-Signature",
                    required = false)
            String signature) {

        // Для проверки подписи мы можем использовать raw body. Здесь предполагается, что
        // framework уже десериализовал в dto — в реальном коде лучше принимать raw String и передавать в verify
        String payloadString = dto.toString(); // замените на реальное тело (raw), если нужен точный расчет подписи

        if (signature != null && !kaspiService.verifyCallbackSignature(payloadString, signature)) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        // обновляем Payment и, если SUCCESS, создаём подписку
        userService.finalizePaymentAndCreateSubscription(dto.paymentId(), dto.status());

        return ResponseEntity.ok("OK");
    }
}
