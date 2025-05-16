package app.dto.statistic;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MeditationStat {
    private Map<String, Integer> meditationStatTags;
    private Map<String, Integer> overwatchedMeditations;
    private int watchedMeditationCount;
    private Map<String, Integer> theMostPrefferedAuthors;
    private PulseStat pulseStat;
}
