package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "parents")
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "mail")
    private String mail;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "registered_at")
    private Timestamp registeredAt;
    @OneToMany(mappedBy = "parent")
    private List<Child> children;
}