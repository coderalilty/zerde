package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import kidd.house.zerde.model.role.Authorities;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @ElementCollection(targetClass = Authorities.class,fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Authorities> authoritiesSet;
    @ManyToMany
    @JoinTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "subjects_id"))
    private List<Subject> subjects;
}

