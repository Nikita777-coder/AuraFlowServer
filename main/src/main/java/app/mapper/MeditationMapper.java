package app.mapper;

import app.dto.meditation.*;
import app.entity.meditation.MeditationEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = TagMapper.class)
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
    @Mapping(target = "author", expression = "java(request.getAuthor() != null ? request.getAuthor() : oldEntity.getAuthor())")
    @Mapping(target = "id", source = "oldEntity.id")
    @Mapping(target = "title", expression = "java(request.getTitle() != null ? request.getTitle() : oldEntity.getTitle())")
    @Mapping(target = "description", expression = "java(request.getTitle() != null ? request.getDescription() : oldEntity.getDescription())")
    @Mapping(target = "videoLink", expression = "java(request.getVideoLink() != null ? request.getVideoLink() : oldEntity.getVideoLink())")
    @Mapping(target = "updateAt", expression = "java(request.getUpdatedAt() != null ? request.getUpdatedAt() : oldEntity.getUpdateAt())")
    @Mapping(target = "jsonTags", ignore = true)
    MeditationEntity updateMeditationEntity(MeditationEntity oldEntity, MeditationUpdateRequest request);
    @Mapping(target = "tags", source = "jsonTags")
    Meditation meditationEntityToMeditation(MeditationEntity meditation);

    @Mapping(target = "videoId", source = "oldEntity.videoId")
    @Mapping(target = "id", source = "oldEntity.id")
    @Mapping(target = "jsonTags", source = "oldEntity.jsonTags")
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
