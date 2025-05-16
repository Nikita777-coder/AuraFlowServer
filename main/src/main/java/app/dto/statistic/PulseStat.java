package app.dto.statistic;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PulseStat {
    private double avgPulse;
    private LocalDate theLowestPulseData;
    private double theLowestPulse;
    private Map<LocalDate, List<Double>> pulseData;
}
