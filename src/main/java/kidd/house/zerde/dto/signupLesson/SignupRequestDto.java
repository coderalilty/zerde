package kidd.house.zerde.dto.signupLesson;

public record SignupRequestDto(String childName,
                               int childAge,
                               String parentName,
                               String parentPhone,
                               String parentEmail,
                               LessonTypeDto lessonTypeDto,
                               String lessonDay) {
}