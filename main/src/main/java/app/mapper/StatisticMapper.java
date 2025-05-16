package app.mapper;

import app.dto.meditation.MeditationStatData;
import app.dto.statistic.Statistic;
import app.entity.MeditationStatEntity;
import app.entity.StatisticEntity;
import app.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatisticMapper {
    MeditationStatEntity meditationStatDataToMeditationStatEntity(MeditationStatData data);
    @Mapping(target = "id", ignore = true)
    StatisticEntity dataToStatisticEntity(UserEntity user, List<MeditationStatEntity> watchedMeditationsPerDay, Statistic statistic);
}
