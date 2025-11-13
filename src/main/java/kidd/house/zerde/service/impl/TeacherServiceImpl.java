package kidd.house.zerde.service.impl;

import kidd.house.zerde.config.PdfCompressorConfig;
import kidd.house.zerde.dto.teacher.*;
import kidd.house.zerde.dto.temporartLessonDto.TeacherLessonsDto;
import kidd.house.zerde.model.entity.*;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.repo.*;
import kidd.house.zerde.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private LessonRepo lessonRepo;
    @Autowired
    private DocumentRepo documentRepo;
    @Autowired
    private TaskRepo taskRepo;
    private final Path uploadDir = Paths.get("C:/uploads/documents");
    public void init() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }
    @Override
    public TeacherProfileDto getTeacherProfiles(int user_id) {
        User user = userRepo.findById(user_id);
        return new TeacherProfileDto(
                user.getId(),
                user.getName(),
                user.getSurName(),
                user.getLastName(),
                user.getEmail(),
                user.getAuthorities()
        );
    }

    @Override
    public List<TeacherSubjectsDto> getTeacherSubjects(int userId, TeacherSubjectsDto teacherSubjects) {
        List<Subject> subjects = subjectRepo.findAllByUsersId(userId);
        return subjects.stream()
                .map(this::toSubjectDto)
                .toList();
    }

    @Override
    public List<TeacherLessonsDto> getTeacherLessons(int userId, TeacherLessonsDto teacherLessonsDto) {
        List<Lesson> lessons = lessonRepo.findAllByUserId(userId);
        return lessons.stream()
                .map(this::toLessonDto)
                .toList();
    }

    @Override
    public TeacherLessonsDto getLesson(int lessonId) {
        Lesson lesson = lessonRepo.findById(lessonId);
        return new TeacherLessonsDto(
                lesson.getLessonDay(),
                lesson.getFrom(),
                lesson.getTo(),
                lesson.getLessonStatus(),
                lesson.getGroupType()
        );
    }

    @Override
    public void editLesson(int lessonId, TeacherEditLesson teacherEditLesson) throws IOException {
        init();

        if (!teacherEditLesson.file().getContentType().equals("application/pdf")) {
            throw new IOException("Можно загружать только PDF файл!");
        }

        String originalFileName = UUID.randomUUID() + "_" + teacherEditLesson.file().getOriginalFilename();
        Path originalPath = uploadDir.resolve(originalFileName);
        Files.copy(teacherEditLesson.file().getInputStream(), originalPath, StandardCopyOption.REPLACE_EXISTING);

        // 📉 Сжимаем PDF
        String compressedFileName = "compressed_" + originalFileName;
        Path compressedPath = uploadDir.resolve(compressedFileName);
        try {
            PdfCompressorConfig.compressPdf(originalPath, compressedPath);
            Files.deleteIfExists(originalPath); // Удаляем исходный несжатый файл
        } catch (Exception e) {
            throw new IOException("Ошибка при сжатии PDF: " + e.getMessage());
        }

        Lesson lesson = lessonRepo.findById(lessonId);
        if (teacherEditLesson.lessonMark() != null) {
            lesson.setLessonMark(teacherEditLesson.lessonMark());
        }
        if (teacherEditLesson.lessonMark2() != null) {
            lesson.setLessonMark2(teacherEditLesson.lessonMark2());
        }

        if (teacherEditLesson.lessonPlanURL() != null) {
            Document document = new Document();
            document.setDocumentName(teacherEditLesson.documentName());
            document.setFilePath(compressedPath.toString()); // путь к сжатому файлу
            document.setUploadDate(LocalDateTime.now());
            documentRepo.save(document);
            lesson.setDocument(document);
        }

        lesson.setLessonStatus(LessonStatus.EDITED);
        lessonRepo.save(lesson);
    }

    @Override
    public void createTask(CreateTaskDto createTaskDto, int subject_id) {
        Task task = new Task();
        task.setTaskName(createTaskDto.taskName());
        task.setYoutubeURL(createTaskDto.youtubeURL());
        task.setTaskText(createTaskDto.taskText());

        Path uploadDir = Paths.get("C:/uploads/tasks");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("❌ Не удалось создать папку загрузки: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать папку загрузки", e);
        }

        // 📷 Фото
        if (createTaskDto.taskPhotoURL() != null && !createTaskDto.taskPhotoURL().isEmpty()) {
            String photoPath = processPhoto(createTaskDto.taskPhotoURL(), uploadDir);
            task.setTaskPhotoURL(photoPath);
            log.info("✅ Фото успешно сохранено: {}", photoPath);
        }

        // 🎧 Аудио
        if (createTaskDto.taskAudio() != null && !createTaskDto.taskAudio().isEmpty()) {
            String audioPath = processAudio(createTaskDto.taskAudio(), uploadDir);
            task.setTaskAudio(audioPath);
            log.info("✅ Аудио успешно сохранено: {}", audioPath);
        }

        Subject subject = subjectRepo.findById(subject_id);
        task.setSubject(subject);

        taskRepo.save(task);
        log.info("📝 Задача '{}' успешно создана для предмета ID={}", task.getTaskName(), subject_id);
    }

    @Override
    public void deleteTask(int taskId) {
        taskRepo.deleteById(taskId);
    }

    @Override
    public void deleteDocument(int documentId) {
        documentRepo.deleteById(documentId);
    }

    @Override
    public void editTask(int taskId, CreateTaskDto createTaskDto) {
        Task task = taskRepo.findById(taskId);
        if (task == null) {
            log.warn("⚠️ Задача с ID={} не найдена", taskId);
            throw new RuntimeException("Задача с ID " + taskId + " не найдена");
        }

        if (createTaskDto.taskName() != null)
            task.setTaskName(createTaskDto.taskName());

        if (createTaskDto.youtubeURL() != null)
            task.setYoutubeURL(createTaskDto.youtubeURL());

        if (createTaskDto.taskText() != null)
            task.setTaskText(createTaskDto.taskText());

        Path uploadDir = Paths.get("C:/uploads/tasks");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Не удалось создать папку загрузки: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать папку загрузки", e);
        }

        // 🖼 Фото
        if (createTaskDto.taskPhotoURL() != null && !createTaskDto.taskPhotoURL().isEmpty()) {
            deleteOldFile(task.getTaskPhotoURL());
            String newPhotoPath = processPhoto(createTaskDto.taskPhotoURL(), uploadDir);
            task.setTaskPhotoURL(newPhotoPath);
            log.info("♻️ Фото задачи ID={} обновлено: {}", taskId, newPhotoPath);
        }

        // 🎧 Аудио
        if (createTaskDto.taskAudio() != null && !createTaskDto.taskAudio().isEmpty()) {
            deleteOldFile(task.getTaskAudio());
            String newAudioPath = processAudio(createTaskDto.taskAudio(), uploadDir);
            task.setTaskAudio(newAudioPath);
            log.info("♻️ Аудио задачи ID={} обновлено: {}", taskId, newAudioPath);
        }

        taskRepo.save(task);
        log.info("✅ Задача ID={} успешно обновлена", taskId);
    }

    @Override
    public void editTeacher(int userId, EditTeacherDto editTeacherDto) {
        User teacher = userRepo.findById(userId);
        if (teacher == null) {
            log.warn("⚠️ Задача с ID={} не найдена", userId);
            throw new RuntimeException("Задача с ID " + userId + " не найдена");
        }
        if (editTeacherDto.name() != null && !editTeacherDto.name().isEmpty()){
            teacher.setName(editTeacherDto.name());
        }
        if (editTeacherDto.surname() != null && !editTeacherDto.surname().isEmpty()){
            teacher.setSurName(editTeacherDto.surname());
        }
        if (editTeacherDto.lastname() != null && !editTeacherDto.lastname().isEmpty()){
            teacher.setLastName(editTeacherDto.lastname());
        }
        if (editTeacherDto.phone() != null && !editTeacherDto.phone().isEmpty()){
            teacher.setPhone(editTeacherDto.phone());
        }
        userRepo.save(teacher);
    }

    private String processPhoto(MultipartFile photo, Path uploadDir) {
        validateFileSize(photo, 10);
        String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        try {
            compressAndSaveImage(photo, filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("Ошибка при обработке фото '{}': {}", photo.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке фото", e);
        }
    }

    private String processAudio(MultipartFile audio, Path uploadDir) {
        validateFileSize(audio, 10);
        String fileName = UUID.randomUUID() + "_" + audio.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        try {
            audio.transferTo(filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("Ошибка при сохранении аудио '{}': {}", audio.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Ошибка при сохранении аудио", e);
        }
    }

    private void validateFileSize(MultipartFile file, int maxSizeMb) {
        long maxSizeBytes = maxSizeMb * 1024L * 1024L;
        if (file.getSize() > maxSizeBytes) {
            log.warn("Файл '{}' превышает допустимый размер {} МБ ({} байт)",
                    file.getOriginalFilename(), maxSizeMb, file.getSize());
            throw new IllegalArgumentException("Размер файла превышает " + maxSizeMb + " МБ");
        }
    }

    private void compressAndSaveImage(MultipartFile imageFile, Path outputPath) throws IOException {
        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("Неверный формат изображения");
        }

        try (OutputStream os = Files.newOutputStream(outputPath);
             ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) throw new IllegalStateException("JPEG writer не найден");

            ImageWriter writer = writers.next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.6f); // 60% качества
            }

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }

        log.info("📸 Фото успешно сжато и сохранено: {}", outputPath);
    }

    private void deleteOldFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        try {
            Path oldFile = Paths.get(filePath);
            if (Files.exists(oldFile)) {
                Files.delete(oldFile);
                log.info("🗑️ Удалён старый файл: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("⚠️ Не удалось удалить старый файл {} — {}", filePath, e.getMessage());
        }
    }

    private TeacherLessonsDto toLessonDto(Lesson lesson) {
        return new TeacherLessonsDto(
                lesson.getLessonDay(),
                lesson.getFrom(),
                lesson.getTo(),
                lesson.getLessonStatus(),
                lesson.getGroupType()
        );
    }

    private TeacherSubjectsDto toSubjectDto(Subject subject) {
        return new TeacherSubjectsDto(
                subject.getName());
    }

}
