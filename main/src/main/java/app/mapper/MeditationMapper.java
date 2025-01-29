package app.mapper;

import app.dto.meditation.*;
import app.entity.meditation.MeditationEntity;
import app.entity.meditation.TagEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MeditationMapper {
    @Mapping(source = "embedLink", target = "videoLink")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "videoId", source = "id")
    MeditationEntity uploadResponseDataToMeditationEntity(UploadData response);

    List<Meditation> meditationEntitiesToMeditations(List<MeditationEntity> meditationEntities);

    @Mapping(target = "videoId", source = "oldEntity.videoId")
    @Mapping(target = "id", source = "oldEntity.id")
    @Mapping(target = "tags", source = "oldEntity.tags")
    @Mapping(target = "videoLink", source = "oldEntity.videoLink")
    @Mapping(target = "durationSeconds", source = "meditationServiceData.duration")
    @Mapping(target = "title", source = "meditationServiceData.title")
    @Mapping(target = "description", source = "meditationServiceData.description")
    @Mapping(target = "createdAt", source = "oldEntity.createdAt")
    @Mapping(target = "status", source = "meditationServiceData.status")
    MeditationEntity meditationServiceDataToMeditationEntity(MeditationServiceData meditationServiceData, MeditationEntity oldEntity);

    default Tag tagEntityToTag(TagEntity entity) {
        if (entity == null || entity.getTag() == null) {
            return null;
        }
        return entity.getTag();
    }

    default List<Tag> tagEntitiesToTags(List<TagEntity> tagEntities) {
        if (tagEntities == null) {
            return Collections.emptyList();
        }
        return tagEntities.stream()
                .map(this::tagEntityToTag)
                .collect(Collectors.toList());
    }

    @AfterMapping
    default void mapTags(@MappingTarget Meditation meditation, MeditationEntity meditationEntity) {
        if (meditationEntity.getTags() != null) {
            meditation.setTags(tagEntitiesToTags(meditationEntity.getTags()));
        }
    }

    @AfterMapping
    default void mapTags(@MappingTarget List<Meditation> meditations, List<MeditationEntity> meditationEntities) {
        for (int i = 0; i < meditations.size(); ++i) {
            mapTags(meditations.get(i), meditationEntities.get(i));
        }
    }

    @AfterMapping
    default void mapMeditationStatus(UploadData uploadData, @MappingTarget MeditationEntity meditation) {
        meditation.setStatus(MeditationStatus.valueOf(uploadData.getStatus().toUpperCase()));
    }
}
