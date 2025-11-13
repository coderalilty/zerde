package kidd.house.zerde.dto.teacher;

import org.springframework.web.multipart.MultipartFile;

public record CreateTaskDto(
        String taskName,
        String youtubeURL,
        String taskText,
        MultipartFile taskPhotoURL,
        MultipartFile taskAudio
) {
}
