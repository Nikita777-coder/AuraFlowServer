package app.service;

import app.dto.meditation.MeditationStatData;
import app.dto.meditation.Status;
import app.dto.statistic.*;
import app.entity.MeditationStatEntity;
import app.entity.StatisticEntity;
import app.extra.ProgramCommons;
import app.mapper.StatisticMapper;
import app.mapper.StatusMapper;
import app.mapper.TagMapper;
import app.repository.StatisticRepository;
import app.repository.UserMeditationRepository;
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
    private final UserMeditationRepository meditationRepository;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final PremiumService premiumService;
    private final TagMapper tagMapper;
    private final StatusMapper statusMapper;
    public UUID create(UserDetails userDetails,
                       Statistic statistic) {
        programCommons.checkUserRole(userDetails);
        var user = userService.getUserByEmail(statistic.getUserEmail());

        List<MeditationStatEntity> meditationStatEntities = null;
        if (statistic.getWatchedMeditations() != null && !statistic.getWatchedMeditations().isEmpty()) {
            meditationStatEntities = parseMeditationStats(statistic);
        }

        return statisticRepository.save(statisticMapper.dataToStatisticEntity(user, meditationStatEntities, statistic)).getId();
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

        return userStatisticOut;
    }
    public Statistic update(UserDetails userDetails, StatisticUpdate statisticUpdate) {
        programCommons.checkUserRole(userDetails);

        var statisticEntity = statisticRepository.findById(statisticUpdate.getStatId());
        if (statisticEntity.isEmpty()) {
            throw new IllegalArgumentException("statistic by specified id not found");
        }

        var user = userService.getUserByEmail(statisticUpdate.getUpdData().getUserEmail());
        var stat = statisticEntity.get();
        stat.setUser(user);

        if (statisticUpdate.getUpdData().getEntranceCountPerDay() > 0 && statisticUpdate.getUpdData().getEntranceCountPerDay() != stat.getEntranceCountPerDay()) {
            stat.setEntranceCountPerDay(statisticUpdate.getUpdData().getEntranceCountPerDay());
        }

        if (statisticUpdate.getUpdData().getWatchedMeditations() != null) {
            stat.setWatchedMeditationsPerDay(parseMeditationStats(statisticUpdate.getUpdData()));
        }

        return statisticMapper.statisticEntityToStatistic(statisticRepository.save(stat));
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

        PulseStat pulseStat = new PulseStat();
        double avgPulse = 0.0, theLowestPulse = Double.MAX_VALUE;
        int pulseCount = 0;
        LocalDate theLowestPulseData = null;
        List<MeditationStatEntity> meditationStatEntities = new ArrayList<>();
        Map<LocalDate, List<Double>> pulseData = new HashMap<>(userStatistic.size());

        for (StatisticEntity stat: userStatistic) {
            boolean theLowest = false;

            pulseData.put(stat.getFixedTime(), new ArrayList<>(stat.getWatchedMeditationsPerDay().size()));
            for (MeditationStatEntity data: stat.getWatchedMeditationsPerDay()) {
                for (String tag: tagMapper.jsonTagsToTags(data.getMeditationEntity().getMeditationFromPlatform().getJsonTags())) {
                    meditationStatTags.putIfAbsent(tag, 0);
                    meditationStatTags.put(tag, meditationStatTags.get(tag) + 1);
                }

                if (statusMapper.stringStatusesToSetOfStatuses(data.getMeditationEntity().getStatuses()).contains(Status.WATCHED)) {
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

                if (Math.min(theLowestPulse, data.getAveragePulse()) == data.getAveragePulse()) {
                    theLowest = true;
                    theLowestPulse = data.getAveragePulse();
                }

                meditationStatEntities.add(data);
                pulseData.get(stat.getFixedTime()).add(data.getAveragePulse());
            }

            if (theLowest) {
                theLowestPulseData = stat.getFixedTime();
            }
        }

        int firstQuartile = (int) Math.ceil(0.25 * meditationStatEntities.size());
        int thirdQuantile = (int) Math.floor(0.75 * meditationStatEntities.size());

        meditationStatEntities.sort(Comparator.comparingDouble(MeditationStatEntity::getAveragePulse));
        for (int i = firstQuartile - 1; i < thirdQuantile; ++i) {
            avgPulse += meditationStatEntities.get(i).getAveragePulse();
            pulseCount++;
        }

        avgPulse /= pulseCount;
        pulseStat.setAvgPulse(avgPulse);
        pulseStat.setTheLowestPulseData(theLowestPulseData);
        pulseStat.setTheLowestPulse(theLowestPulse);
        pulseStat.setPulseData(pulseData);

        meditationStat.setMeditationStatTags(meditationStatTags);
        meditationStat.setOverwatchedMeditations(overwatchedMeditations);
        meditationStat.setWatchedMeditationCount(watchedMeditationCount);
        meditationStat.setTheMostPrefferedAuthors(theMostPrefferedAuthors);
        meditationStat.setPulseStat(pulseStat);

        return meditationStat;
    }

    private List<MeditationStatEntity> parseMeditationStats(Statistic statistic) {
        List<MeditationStatEntity> meditationStatEntities = new ArrayList<>(statistic.getWatchedMeditations().size());

        for (MeditationStatData data : statistic.getWatchedMeditations()) {
            var userMeditationEntity = meditationRepository.findById(data.getMeditationId());
            if (userMeditationEntity.isEmpty()) {
                throw new IllegalArgumentException(String.format("meditation with id %s not found", data.getMeditationId()));
            }

            if (data.getAveragePulse() < 40) {
                throw new IllegalArgumentException(String.format("meditation with id %s oops, person pulse can't be < 40, " +
                        "in this state person fill oxygen starvation", data.getMeditationId()));
            }

            meditationStatEntities.add(statisticMapper.meditationStatDataToMeditationStatEntity(data));
            meditationStatEntities.get(meditationStatEntities.size() - 1).setMeditationEntity(userMeditationEntity.get());
        }

        return meditationStatEntities;
    }
}
