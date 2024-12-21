package app.dto.meditation;

import java.time.LocalDateTime;
import java.util.List;

public class Meditation {
    private String author;
    private String title;
    private String link;
    private List<Tag> tags;
    private LocalDateTime addingTime;
}
