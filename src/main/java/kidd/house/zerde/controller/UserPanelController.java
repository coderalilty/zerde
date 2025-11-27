package kidd.house.zerde.controller;

import kidd.house.zerde.dto.user.*;
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
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfiles(){
        UserProfileDto userProfileDto= userService.getUserProfiles();
        return ResponseEntity.ok(userProfileDto);
    }
/*    @PutMapping("/edit_user/{teacher_id}")
    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody EditTeacherDto editTeacherDto){
        userService.editTeacher(teacher_id,editTeacherDto);
        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
    }*/
    @PutMapping("/edit_user/{user_id}")
    public ResponseEntity<String> editUser(@PathVariable int user_id,@RequestBody EditUserDto editUserDto) {
        userService.editUser(user_id, editUserDto);
        return new ResponseEntity<>("User edited", HttpStatus.OK);
    }
    @GetMapping("/subscription/{user_id}")
    public ResponseEntity<SubscriptionDto> getUserSubscription(@PathVariable int user_id) {
        SubscriptionDto subscription = userService.getUserSubscription(user_id);
        if (subscription == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscription);
    }

    // Подписка сатып алу
    @PostMapping("/buy-subscription")
    public ResponseEntity<String> buySubscription(@RequestParam int userId,
                                                  @RequestParam int subscriptionPlanId) {
        userService.buySubscription(userId, subscriptionPlanId);
        return ResponseEntity.ok("Subscription purchased successfully");
    }

    // Уақытша сабақтарды шығару
    @GetMapping("/trial_lesson/{user_id}")
    public ResponseEntity<TrialLessonDto> getTrialLesson(@PathVariable int user_id) {
        TrialLessonDto trialLesson = userService.getTrialLesson(user_id);
        if (trialLesson == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trialLesson);
    }

    // Постоянный сабақтарды шығару
    @GetMapping("/permanent_lessons/{user_id}")
    public ResponseEntity<List<LessonDto>> getPermanentLessons(@PathVariable int user_id) {
        List<LessonDto> lessons = userService.getPermanentLessons(user_id);
        return ResponseEntity.ok(lessons);
    }

}