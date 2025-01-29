package app.entity.meditation;

import app.dto.meditation.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private UUID id;

    @Column
    private Tag tag;
}
