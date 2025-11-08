package kidd.house.zerde.controller;

import kidd.house.zerde.dto.adminDto.LockLessonDto;
import kidd.house.zerde.dto.temporartLessonDto.CalendarDayDto;
import kidd.house.zerde.dto.temporartLessonDto.TemporaryLessonDto;
import kidd.house.zerde.service.LessonService;
import kidd.house.zerde.service.TrialLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trial")
@RequiredArgsConstructor
public class TrialLessonsPanelController {
    private final TrialLessonService trialLessonService;
    private final LessonService lessonService;
    @GetMapping("/lock-lesson")
    public ResponseEntity<List<LockLessonDto>> getLockLesson(){
        List<LockLessonDto> lockLesson = lessonService.getLockLesson();
        return  ResponseEntity.ok(lockLesson);
    }
    @GetMapping("/get_trial_lesson/{userId}")
    public ResponseEntity<List<TemporaryLessonDto>> getTrialLesson(@PathVariable Integer userId){
        List<TemporaryLessonDto> temporaryLessonDto = lessonService.getTrialLesson(userId);
        return ResponseEntity.ok(temporaryLessonDto);
    }
    @GetMapping("/get_calendar")
    public ResponseEntity<List<CalendarDayDto>> getCalendar(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam String roomName
    ) {
        List<CalendarDayDto> calendar = lessonService.getCalendar(year, month, roomName);
        return ResponseEntity.ok(calendar);
    }
    @PostMapping("/create_trial_lesson")
    public ResponseEntity<String> create_trial_lesson(@RequestBody TemporaryLessonDto temporaryLessonDto){
        trialLessonService.createTrialLesson(temporaryLessonDto);
        trialLessonService.sendNotification(temporaryLessonDto);
        return new ResponseEntity<>("Временный урок успешно сохранен!", HttpStatus.CREATED);
    }

}
