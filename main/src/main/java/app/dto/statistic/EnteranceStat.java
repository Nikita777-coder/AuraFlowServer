package app.dto.statistic;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EnteranceStat {
    private long totalEntarance;
    private long maxEntarancePerDay;
    private LocalDate maxEntarancePerDayDate;
}
