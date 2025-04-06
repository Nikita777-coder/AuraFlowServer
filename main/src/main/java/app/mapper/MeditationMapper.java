package app.mapper;

import app.dto.meditation.*;
import app.entity.meditation.MeditationEntity;
import app.entity.meditation.TagEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MeditationMapper {
    @Mapping(source = "uploadResponse.data.embedLink", target = "videoLink")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "videoId", source = "uploadResponse.data.id")
    @Mapping(target = "title", source = "uploadResponse.data.title")
    @Mapping(target = "description", source = "uploadResponse.data.description")
    @Mapping(target = "createdAt", source = "uploadResponse.data.createdAt")
    MeditationEntity uploadResponseDataToMeditationEntity(UploadResponseFull response);

    List<Meditation> meditationEntitiesToMeditations(List<MeditationEntity> meditationEntities);

    @Mapping(target = "videoId", source = "oldEntity.videoId")
    @Mapping(target = "id", source = "oldEntity.id")
    @Mapping(target = "tags", source = "oldEntity.tags")
    @Mapping(target = "durationSeconds", source = "meditationServiceData.duration")
    @Mapping(target = "title", source = "oldEntity.title")
    @Mapping(target = "description", source = "oldEntity.description")
    @Mapping(target = "createdAt", source = "oldEntity.createdAt")
    @Mapping(target = "status", source = "meditationServiceData.status")
    @Mapping(target = "videoLink", source = "oldEntity.videoLink")
    MeditationEntity meditationServiceDataToMeditationEntity(MeditationServiceData meditationServiceData, MeditationEntity oldEntity);

    @Mapping(target = "wasUploadFromUrl", source = "wasUploadedFromUrl")
    @Mapping(target = "uploadResponse.data.id", source = "videoId")
    @Mapping(target = "uploadResponse.data.title", source = "title")
    @Mapping(target = "uploadResponse.data.description", source = "description")
    @Mapping(target = "uploadResponse.data.status", source = "status")
    @Mapping(target = "uploadResponse.data.embedLink", source = "videoLink")
    UploadResponseFull meditationEntityToUploadResponseFull(MeditationEntity meditation);

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
    default void mapNullFiledsOfMeditationEntity(UploadResponseFull uploadData, @MappingTarget MeditationEntity meditation) {
        if (uploadData.getUploadResponse().getData().getStatus() != null) {
            meditation.setStatus(MeditationStatus.valueOf(uploadData.getUploadResponse().getData().getStatus().toUpperCase()));
        }

        if (uploadData.getUploadResponse().getData().getCreatedAt() == null) {
            meditation.setCreatedAt(LocalDateTime.now());
        }

        meditation.setAuthor("service");
    }
}
