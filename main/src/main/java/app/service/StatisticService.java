package app.service;

import app.dto.meditation.MeditationStatData;
import app.dto.meditation.Status;
import app.dto.statistic.Period;
import app.dto.statistic.Statistic;
import app.entity.MeditationStatEntity;
import app.entity.StatisticEntity;
import app.extra.ProgramCommons;
import app.mapper.StatusMapper;
import app.mapper.TagMapper;
import app.repository.MeditationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UserService userService;
    private final ProgramCommons programCommons;
    private final MeditationRepository meditationRepository;
    private final StatisticRepository statisticRepository;
    private final MeditationStatRepository meditationStatRepository;
    private final StatisticMapper statisticMapper;
    private final PremiumService premiumService;
    private final TagMapper tagMapper;
    private final StatusMapper statusMapper;
    public UUID create(UserDetails userDetails,
                       Statistic statistic) {
        if (!programCommons.isUserAdmin(userDetails)) {
            throw new AccessDeniedException("Access denied");
        }

        var user = userService.getUserByEmail(statistic.getUserEmail());

        List<MeditationStatEntity> meditationStatEntities = null;
        if (statistic.getWatchedMeditationsPerDay() != null && !statistic.getWatchedMeditationsPerDay().isEmpty()) {
            meditationStatEntities = new ArrayList<>(statistic.getWatchedMeditationsPerDay().size());

            for (MeditationStatData data : statistic.getWatchedMeditationsPerDay()) {
                if (meditationRepository.findById(data.getMeditationId()).isEmpty()) {
                    throw new IllegalArgumentException(String.format("meditation with id %s not found", data.getMeditationId()));
                }

                if (data.getAveragePulse() < 40) {
                    throw new IllegalArgumentException(String.format("meditation with id %s oops, person pulse can't be < 40, " +
                            "in this state person fill oxygen starvation", data.getMeditationId()));
                }

                meditationStatEntities.add(statisticMapper.meditationStatDataToMeditationStatEntity(data));
            }
        }

        return statisticRepository.save(statisticMapper.dataToStatisticEntity(user, statistic, meditationStatEntities)).getId();
    }

    public UserStatistic getUserStatistic(UserDetails userDetails, Period period) {
        if (!premiumService.hasPremium(userDetails)) {
            throw new AccessDeniedException("you don't have subscription!");
        }

        List<StatisticEntity> userStatistic = statisticRepository.findAllByUserAndFixedTimeBetween(
                userService.getUserByEmail(userDetails.getUsername()),
                period.getFrom(),
                period.getTo()
        );

        UserStatistic userStatisticOut = new UserStatistic();
        userStatisticOut.setEnterenceStat(getEnterenceStat(userStatistic));
        userStatisticOut.setMeditationStat(getMeditationStat(userStatistic));
        // userStatisticOut.setBreathePractiseStat(getBreathePractiseStat(userStatistic))
        userStatisticOut.setPeriod(period);
    }
    private EnteranceStat getEnterenceStat(List<StatisticEntity> userStatistic) {
        EnteranceStat enteranceStat = new EnteranceStat();

        long totalEnterance = 0, maxEnterancePerDay = 0;
        LocalDate maxEnterancePerDayDate = null;

        for (StatisticEntity stat: userStatistic) {
            if (stat.getEntranceCountPerDay() > 0) {
                totalEnterance += stat.getEntranceCountPerDay();

                if (Math.max(maxEnterancePerDay, stat.getEntranceCountPerDay()) == stat.getEntranceCountPerDay()) {
                    maxEnterancePerDay = stat.getEntranceCountPerDay();
                    maxEnterancePerDayDate = stat.getStatisticTimeFixing();
                }
            }
        }

        enteranceStat.setTotalEntarance(totalEnterance);
        enteranceStat.setMaxEntarancePerDay(maxEnterancePerDay);
        enteranceStat.setMaxEntarancePerDayDate(maxEnterancePerDayDate);

        return enteranceStat;
    }
    private MeditationStat getMeditationStat(List<StatisticEntity> userStatistic) {
        MeditationStat meditationStat = new MeditationStat();

        Map<String, Integer> meditationStatTags = new HashMap<>();
        Map<String, Integer> overwatchedMeditations = new HashMap<>();
        int watchedMeditationCount = 0;
        Map<String, Integer> theMostPrefferedAuthors = new HashMap<>();

        // targetPulse
        // avgPulse
        // theLowestValue
        PulseStat pulseStat = new PulseStat();
        double avgPulse = 0.0;
        int theLowestPulse = Integer.MAX_VALUE, pulseCount = 0;
        LocalDate theLowestPulseData = null;

        for (StatisticEntity stat: userStatistic) {
            boolean theLowest = false;
            for (MeditationStatEntity data: stat.getWatchedMeditationsPerDay()) {
                for (String tag: tagMapper.jsonTagsToTags(data.getMeditationEntity().getMeditationFromPlatform().getJsonTags())) {
                    meditationStatTags.putIfAbsent(tag, 0);
                    meditationStatTags.put(tag, meditationStatTags.get(tag) + 1);
                }

                if (statisticMapper.stringStatusesToSetOfStatuses(data.getMeditationEntity().getStatuses()).containsKey(Status.WATCHED)) {
                    overwatchedMeditations.putIfAbsent(data.getMeditationEntity().getTitle(), 0);
                    meditationStatTags.put(
                            data.getMeditationEntity().getTitle(),
                            meditationStatTags.get(data.getMeditationEntity().getTitle()) + 1
                    );

                    watchedMeditationCount++;
                }

                theMostPrefferedAuthors.putIfAbsent(data.getMeditationEntity().getMeditationFromPlatform().getAuthor(), 0);
                theMostPrefferedAuthors.put(
                        data.getMeditationEntity().getMeditationFromPlatform().getAuthor(),
                        theMostPrefferedAuthors.get(data.getMeditationEntity().getMeditationFromPlatform().getAuthor()) + 1
                );

                avgPulse += data.getPulse();
                pulseCount++;

                if (Math.min(theLowestPulse, data.getPulse()) == data.getPulse()) {
                    theLowest = true;
                    theLowestPulse = data.getPulse();
                }
            }

            if (theLowest) {
                theLowestPulseData = stat.getFixedTime();
            }
        }

        avgPulse /= pulseCount;
        pulseStat.setAvgPulse(avgPulse);
        pulseStat.setTheLowestPulseData(theLowestPulseData);
        pulseStat.setTheLowestPulse(theLowestPulse);

        meditationStat.setMeditationStatTags(meditationStatTags);
        meditationStat.setOverwatchedMeditations(overwatchedMeditations);
        meditationStat.setWatchedMeditationCount(watchedMeditationCount);
        meditationStat.setTheMostPrefferedAuthors(theMostPrefferedAuthors);
        meditationStat.setPulseStat(pulseStat);

        return meditationStat;
    }
}
