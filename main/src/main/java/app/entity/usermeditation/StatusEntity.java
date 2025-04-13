package app.entity.usermeditation;

import app.dto.meditation.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "statuses")
@Getter
@Setter
@NoArgsConstructor
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "status_name")
    private Status status;
}
