package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @ManyToMany
    @JoinTable(name = "teacher_subjects",joinColumns = @JoinColumn(name = "subjects_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id"))
    private List<User> users;
}
