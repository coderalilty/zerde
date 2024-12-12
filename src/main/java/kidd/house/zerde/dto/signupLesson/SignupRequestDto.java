package kidd.house.zerde.dto.signupLesson;

public record SignupRequestDto(String childName,
                               String diagnosis,
                               int age,
                               String parentName,
                               String parentPhone,
                               String parentEmail,
                               LessonTypeDto lessonTypeDto,
                               String lessonDay,
                               String lessonTime) {
}
