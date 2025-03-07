package app.dto.meditation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Meditation {
    private String author;
    private String title;
    private String description;
    private String videoLink;
    private List<Tag> tags;
    private LocalDateTime createdAt;
}
