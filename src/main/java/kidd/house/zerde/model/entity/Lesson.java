package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import kidd.house.zerde.model.status.LessonStatus;
import kidd.house.zerde.model.type.GroupType;
import kidd.house.zerde.model.type.LessonType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "create_date")
    private String from;
    @Column(name = "update_date")
    private String to;
    @Column(name = "lesson_name")
    private String lessonName;
    @Column(name = "lesson_day")
    private String lessonDay;
    @Column(name = "lesson_time")
    private String lessonTime;
    @Column(name = "lesson_type")
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;
    @Column(name = "lesson_status")
    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;
    @Column(name = "group_type")
    @Enumerated(EnumType.STRING)
    private GroupType groupType;
    @OneToMany(mappedBy = "lesson",cascade = CascadeType.ALL)
    private List<Child> children = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "parents_id")
    private Parent parent;
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