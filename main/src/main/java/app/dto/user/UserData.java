package app.dto.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Builder
@Getter
public class UserData {
    // Email need to ignore In service when input.
    // In output ok
    private String email;
    private String name;
    private Boolean hasPractiseBreathOpt;
    private Boolean hasOpenAppOpt;
    private LocalTime startTimeOfBreathPractise;
    private LocalTime stopTimeOfBreathPractise;
    private Integer countBreathPractiseReminderPerDay;
}
