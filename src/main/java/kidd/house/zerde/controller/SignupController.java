package kidd.house.zerde.controller;

import kidd.house.zerde.dto.signupLesson.*;
import kidd.house.zerde.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/signup-lesson")
@RequiredArgsConstructor
public class SignupController {
    private final SignupService signupService;
    @GetMapping
    public ResponseEntity<List<SignUpLessonResponse>> signUpLesson(){
        List<SignUpLessonResponse> lessonResponses = signupService.getLessons();
        return ResponseEntity.ok(lessonResponses);
    }
    @PostMapping
    public ResponseEntity<SignupResponse> createSignupLesson(@RequestBody SignupRequestDto signupRequest) {
        // Сохраняем запись в базе со статусом "success"
        String status = signupService.saveSignup(signupRequest, "success");

            signupService.sendNotification(signupRequest);


        return ResponseEntity.ok(new SignupResponse(status, "Запись успешно обработана"));
    }
}
