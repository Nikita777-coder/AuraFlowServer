package app.mapper;

import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationPlatformAlbumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {MeditationMapper.class, TagMapper.class}
)
public interface MeditationPlatformAlbumMapper {
    MeditationPlatformAlbumEntity meditationAlbumRequestToMeditationPlatformAlbumEntity(MeditationAlbumRequest meditationAlbumRequest);
    @Mapping(target = "meditations", source = "entity.meditationsFromPlatform")
    @Mapping(target = "ownerId", source = "entity.userEntity.id")
    MeditationAlbumPlatform meditationPlatformAlbumEntityToMeditationAlbumPlatform(MeditationPlatformAlbumEntity entity);
    List<MeditationAlbumPlatform> meditationPlatformAlbumEntitiesToMeditationAlbumsPlatform(List<MeditationPlatformAlbumEntity> entities);
    @Mapping(target = "title", expression = "java(title(request, entity))")
    @Mapping(target = "description", expression = "java(description(request, entity))")
    @Mapping(target = "meditations", source = "request.meditations")
    MeditationAlbumRequest prepareMeditationAlbumRequestFromOldMeditationPlatformAlbumEntity(MeditationAlbumRequest request, MeditationPlatformAlbumEntity entity);
    default String title(MeditationAlbumRequest request, MeditationPlatformAlbumEntity entity) {
        if (request.getTitle() == null) {
            return entity.getTitle();
        }

        return request.getTitle();
    }
    default String description(MeditationAlbumRequest request, MeditationPlatformAlbumEntity entity) {
        if (request.getDescription() == null) {
            return entity.getDescription();
        }

        return request.getDescription();
    }
}
