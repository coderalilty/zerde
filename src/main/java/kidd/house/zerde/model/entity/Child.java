package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "children")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "age")
    private int age;
    @ManyToOne
    @JoinColumn(name = "diagnoses_id",referencedColumnName = "id")
    private Diagnosis diagnosis;
    @ManyToOne
    @JoinColumn(name = "users_id",referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "app_groups_id",referencedColumnName = "id")
    private Group group;
}

