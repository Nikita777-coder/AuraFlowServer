package app.mapper;

import app.dto.meditation.*;
import app.entity.MeditationEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = TagMapper.class)
public interface MeditationMapper {
    @Mapping(source = "response.uploadResponse.data.embedLink", target = "videoLink")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", source = "response.status")
    @Mapping(target = "title", source = "response.uploadResponse.data.title")
    @Mapping(target = "description", source = "response.uploadResponse.data.description")
    @Mapping(target = "wasUploadedFromUrl", source = "response.wasUploadFromUrl")
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

    @Mapping(target = "id", source = "oldEntity.id")
    @Mapping(target = "jsonTags", source = "oldEntity.jsonTags")
    @Mapping(target = "title", expression = "java(request.getUploadResponse().getData().getTitle() != null ? request.getUploadResponse().getData().getTitle() : oldEntity.getTitle())")
    @Mapping(target = "description", expression = "java(request.getUploadResponse().getData().getDescription() != null ? request.getUploadResponse().getData().getDescription() : oldEntity.getDescription())")
    @Mapping(target = "createdAt", expression = "java(request.getUploadResponse().getData().getCreatedAt() != null ? request.getUploadResponse().getData().getCreatedAt() : oldEntity.getCreatedAt())")
    @Mapping(target = "status", expression = "java(request.getStatus() != null ? request.getStatus() : oldEntity.getStatus())")
    @Mapping(target = "videoLink", expression = "java(request.getUploadResponse().getData().getEmbedLink() != null ? request.getUploadResponse().getData().getEmbedLink() : oldEntity.getVideoLink())")
    @Mapping(target = "wasUploadedFromUrl", expression = "java(oldEntity.isWasUploadedFromUrl())")
    MeditationEntity meditationServiceDataToMeditationEntity(UploadResponseFull request, MeditationEntity oldEntity);

//    @Mapping(target = "wasUploadFromUrl", source = "wasUploadedFromUrl")
//    @Mapping(target = "uploadResponse.data.id", source = "videoId")
//    @Mapping(target = "uploadResponse.data.title", source = "title")
//    @Mapping(target = "uploadResponse.data.description", source = "description")
//    @Mapping(target = "uploadResponse.data.status", source = "status")
//    @Mapping(target = "uploadResponse.data.embedLink", source = "videoLink")
//    UploadResponseFull meditationEntityToUploadResponseFull(MeditationEntity meditation);

//    @AfterMapping
//    default void mapNullFiledsOfMeditationEntity(UploadResponseFull uploadData, @MappingTarget MeditationEntity meditation) {
//        if (uploadData.getUploadResponse().getData().getStatus() != null) {
//            meditation.setStatus(MeditationStatus.valueOf(uploadData.getUploadResponse().getData().getStatus().toUpperCase()));
//        }
//
//        if (uploadData.getUploadResponse().getData().getCreatedAt() == null) {
//            meditation.setCreatedAt(LocalDateTime.now());
//        }
//
//        meditation.setAuthor("service");
//    }
}
