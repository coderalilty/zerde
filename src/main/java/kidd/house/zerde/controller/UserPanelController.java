package kidd.house.zerde.controller;

import kidd.house.zerde.dto.payments.PurchaseSubscriptionDto;
import kidd.house.zerde.model.entity.Subscription;
import kidd.house.zerde.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserPanelController {
    private final UserService userService;
//    @GetMapping("/profile")
//    public ResponseEntity<UserProfileDto> getUserProfiles(){
//          = userService.getUserProfiles();
//        return ResponseEntity.ok();
//    }
//    @PutMapping("/edit_user/{teacher_id}")
//    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody EditTeacherDto editTeacherDto){
//        userService.editTeacher(teacher_id,editTeacherDto);
//        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
//    }
     @PostMapping("/purchase")
     public ResponseEntity<Subscription> purchase(@RequestBody PurchaseSubscriptionDto dto) {
          Subscription subscription = userService.purchase(dto);
          return ResponseEntity.ok(subscription);
     }
}
