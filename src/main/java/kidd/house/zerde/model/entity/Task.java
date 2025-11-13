package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "task_name")
    private String taskName;
    @Column(name = "youtube_url")
    private String youtubeURL;
    @Column(name = "task_text")
    private String taskText;
    @Column(name = "task_photo_url")
    private String taskPhotoURL;
    @Column(name = "task_audio")
    private String taskAudio;
    @ManyToOne
    @JoinColumn(name = "subjects_id",referencedColumnName = "id")
    private Subject subject;
}
