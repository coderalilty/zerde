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
}