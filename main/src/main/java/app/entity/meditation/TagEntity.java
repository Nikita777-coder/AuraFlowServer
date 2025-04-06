package app.entity.meditation;

import app.dto.meditation.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "tags")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TagEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private Tag tag;
}
