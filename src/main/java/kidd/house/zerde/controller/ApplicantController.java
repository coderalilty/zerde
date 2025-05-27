package kidd.house.zerde.controller;

import kidd.house.zerde.dto.signupLesson.*;
import kidd.house.zerde.model.record.LessonDay;
import kidd.house.zerde.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/signup-lesson")
@RequiredArgsConstructor
public class ApplicantController {
    private final SignupService signupService;
    @GetMapping
    public ResponseEntity<List<SignUpLessonResponse>> signUpLesson(){
        List<SignUpLessonResponse> lessonResponses = new ArrayList<>();
        lessonResponses.add(new SignUpLessonResponse(
                new LessonDay("Понидельник","2024-10-17"),
                new LessonTypeDto("PERMANENT",
                        List.of(new FreeLesson("2024-10-17 10:00", "2024-10-17 11:00"))
                )));
        lessonResponses.add(new SignUpLessonResponse(
                new LessonDay("Понидельник","2024-10-17"),
                new LessonTypeDto("frgh",
                        List.of(new FreeLesson("2024-10-17 10:00", "2024-10-17 11:00"))
                )));
        lessonResponses.add(new SignUpLessonResponse(
                new LessonDay("Понидельник","2024-10-17"),
                new LessonTypeDto("PERMANENT",
                        List.of(new FreeLesson("2024-10-17 10:00", "2024-10-17 11:00"))
                )));
        return ResponseEntity.ok(lessonResponses);
    }
    @PostMapping
    public ResponseEntity<SignupResponse> createSignupLesson(@RequestBody SignupRequestDto signupRequest) {
        // Сохраняем запись в базе со статусом "draft"
        String status = signupService.saveSignup(signupRequest, "draft");

        // Проверяем условия для изменения статуса на "success"
        boolean verificationSuccessful = signupService.verifySignup(signupRequest);

        if (verificationSuccessful) {
            status = signupService.updateStatus(signupRequest, "success");
            signupService.sendNotification(signupRequest);
        }

        return ResponseEntity.ok(new SignupResponse(status, "Запись успешно обработана"));
    }
}
