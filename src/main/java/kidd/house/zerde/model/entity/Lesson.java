package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.GroupType;
import kidd.house.zerde.model.type.LessonType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime from;
    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime to;
    @Column(name = "lesson_type")
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;
    @Column(name = "lesson_status")
    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;
    @Column(name = "group_type")
    @Enumerated(EnumType.STRING)
    private GroupType groupType;
    @OneToMany(mappedBy = "lesson")
    private List<Child> children;
    @OneToOne
    @JoinColumn(name = "subjects_id")
    private Subject subject;
    @OneToOne
    @JoinColumn(name = "rooms_id")
    private Room room;
    @OneToOne
    @JoinColumn(name = "users_id")
    private User user;
}