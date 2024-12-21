package app.dto.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserOptions {
    private Boolean hasPractiseBreathOpt;
    private Boolean hasOpenAppOpt;
}
