package kidd.house.zerde.controller;

import kidd.house.zerde.dto.adminDto.AdminProfileDto;
import kidd.house.zerde.dto.adminDto.CreateTeacherDto;
import kidd.house.zerde.dto.teacher.EditTeacherDto;
import kidd.house.zerde.dto.user.UserProfileDto;
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
          = userService.getUserProfiles();
        return ResponseEntity.ok();
    }
    @PutMapping("/edit_user/{teacher_id}")
    public ResponseEntity<String> editTeacher(@PathVariable int teacher_id, @RequestBody EditTeacherDto editTeacherDto){
        userService.editTeacher(teacher_id,editTeacherDto);
        return new ResponseEntity<>("Teacher edited", HttpStatus.OK);
    }
}
