package kidd.house.zerde.controller;

import kidd.house.zerde.dto.user.*;
import kidd.house.zerde.service.FreedomPayService;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserPanelController {
    private final UserService userService;
    private final FreedomPayService freedomPayService;
    @GetMapping("/profile/{user_id}")
    public ResponseEntity<UserProfileDto> getUserProfiles(@PathVariable int user_id) {
        UserProfileDto userProfileDto = userService.getUserProfiles(user_id);
        return ResponseEntity.ok(userProfileDto);
    }
    @GetMapping("/subscriptions/{user_id}")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable int user_id) {
        return userService.getUserSubscriptions(user_id);
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
    @PostMapping("/init")
    public ResponseEntity<Map<String, String>> initPayment(@RequestBody OrderRequest request) {
        // Вызываем сервис для получения ссылки на оплату
        String redirectUrl = freedomPayService.createPayment(
                request.orderId(),
                request.amount(),
                "Оплата заказа №" + request.orderId()
        );

        // Возвращаем ссылку фронтенду, чтобы он сделал window.location.href = redirectUrl
        return ResponseEntity.ok(Map.of("url", redirectUrl));
    }

    // Эндпоинт для Result URL (Webhook от FreedomPay)
    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestParam Map<String, String> allRequestParams) {
        // 1. Проверить подпись (важно!)
        // 2. Если все ок, обновить статус заказа в БД
        System.out.println("Получено уведомление от FreedomPay: " + allRequestParams);

        return ResponseEntity.ok("<response><pg_status>ok</pg_status></response>");
    }
    @PutMapping("/edit_user/{user_id}")
    public ResponseEntity<String> editUser(@PathVariable int user_id, @RequestBody EditUserDto editUserDto) {
        userService.editUser(user_id, editUserDto);
        return new ResponseEntity<>("User edited", HttpStatus.OK);
    }
}
