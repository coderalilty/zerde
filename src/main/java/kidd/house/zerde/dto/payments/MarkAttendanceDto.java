package kidd.house.zerde.dto.payments;

public record MarkAttendanceDto(
        Integer lessonId,
        Integer subscriptionId,
        String childStatus,// CAME, MISSED, EXCUSED
        boolean excused
) {
}
