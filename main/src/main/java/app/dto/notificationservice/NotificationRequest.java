package app.dto.notificationservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class NotificationRequest {
    private List<String> to;
    private String message;
}
