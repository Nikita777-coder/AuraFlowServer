package app.dto.statistic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatistic {
    private EnteranceStat enterenceStat;
    private MeditationStat meditationStat;
    private Period period;
}
