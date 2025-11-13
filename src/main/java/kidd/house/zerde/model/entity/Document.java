package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "document_name")
    private String documentName; // Имя оригинального файла
    @Column(name = "file_path", nullable = false)
    private String filePath; // Путь к файлу на диске (например: C:/uploads\documents\lesson.pdf)
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;
}
