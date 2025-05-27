package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "locked_slot")
public class LockedSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "room_name")
    private String roomName;
    @Column(name = "locked_from")
    private String lockedFrom;
    @Column(name = "locked_to")
    private String lockedTo;
}
