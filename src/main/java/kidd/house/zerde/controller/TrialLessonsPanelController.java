package kidd.house.zerde.controller;

import kidd.house.zerde.dto.temporartLessonDto.TemporaryLessonDto;
import kidd.house.zerde.service.TrialLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trial")
@RequiredArgsConstructor
public class TrialLessonsPanelController {
    private final TrialLessonService trialLessonService;
    @PostMapping("/create_trial_lesson")
    public ResponseEntity<String> create_trial_lesson(@RequestBody TemporaryLessonDto temporaryLessonDto){
        trialLessonService.createTrialLesson(temporaryLessonDto);
        trialLessonService.sendNotification(temporaryLessonDto);
        return new ResponseEntity<>("Временный урок успешно сохранен!", HttpStatus.CREATED);
    }
}
