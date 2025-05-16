package app.dto.notificationservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class NotificationRequest {
    private List<String> listTo;
    private String to;
    private String message;
}
