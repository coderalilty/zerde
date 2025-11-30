package kidd.house.zerde.controller;

import kidd.house.zerde.dto.user.EditUserDto;
import kidd.house.zerde.dto.user.KaspiPaymentResponseDto;
import kidd.house.zerde.dto.user.PurchaseSubscriptionDto;
import kidd.house.zerde.dto.user.UserProfileDto;
import kidd.house.zerde.model.entity.Lesson;
import kidd.house.zerde.model.entity.Subscription;
import kidd.house.zerde.model.entity.User;
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
    @GetMapping("/{user_id}/subscriptions")
    public List<Subscription> getUserSubscriptions(@PathVariable int user_id) {
        return userService.getUserSubscriptions(user_id);
    }
    @PostMapping("/buy-subscription")
    public ResponseEntity<KaspiPaymentResponseDto> buySubscription(@RequestBody PurchaseSubscriptionDto dto) {
        KaspiPaymentResponseDto resp = userService.purchase(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{user_id}/trial_lesson")
    public List<Lesson> getTrialLessons(@PathVariable int user_id) {
        return userService.getTrialLessons(user_id);
    }

    @GetMapping("/{user_id}/permanent_lessons")
    public List<Lesson> getPermanentLessons(@PathVariable int user_id) {
        return userService.getPermanentLessons(user_id);
    }
}
