package app.mapper;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumPlatform;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.MeditationAlbumEntity;
import app.entity.MeditationPlatformAlbumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = MeditationMapper.class)
public interface MeditationAlbumMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "meditations", ignore = true)
    MeditationAlbumEntity meditationAlbumRequestToMeditationAlbumEntity(MeditationAlbumRequest meditationAlbumRequest);
    @Mapping(target = "ownerId", source = "entity.user.id")
    MeditationAlbum meditationAlbumEntityToMeditationAlbum(MeditationAlbumEntity entity);
    List<MeditationAlbum> meditationAlbumEntitiesToMeditationAlbums(List<MeditationAlbumEntity> entities);
    @Mapping(target = "title", expression = "java(title(request, entity))")
    @Mapping(target = "description", expression = "java(description(request, entity))")
    @Mapping(target = "meditations", source = "request.meditations")
    MeditationAlbumRequest prepareMeditationAlbumRequestFromOldMeditationAlbumEntity(MeditationAlbumRequest request, MeditationAlbumEntity entity);
    default String title(MeditationAlbumRequest request, MeditationAlbumEntity entity) {
        if (request.getTitle() == null) {
            return entity.getTitle();
        }

        return request.getTitle();
    }
    default String description(MeditationAlbumRequest request, MeditationAlbumEntity entity) {
        if (request.getDescription() == null) {
            return entity.getDescription();
        }

        return request.getDescription();
    }
}
