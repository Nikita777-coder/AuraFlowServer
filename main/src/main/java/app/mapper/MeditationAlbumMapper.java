package app.mapper;

import app.dto.meditationalbum.MeditationAlbum;
import app.dto.meditationalbum.MeditationAlbumRequest;
import app.entity.UserMeditationAlbumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = UserMeditationMapper.class)
public interface MeditationAlbumMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "meditations", ignore = true)
    UserMeditationAlbumEntity meditationAlbumRequestToMeditationAlbumEntity(MeditationAlbumRequest meditationAlbumRequest);
    @Mapping(target = "ownerId", source = "entity.user.id")
    MeditationAlbum meditationAlbumEntityToMeditationAlbum(UserMeditationAlbumEntity entity);
    List<MeditationAlbum> meditationAlbumEntitiesToMeditationAlbums(List<UserMeditationAlbumEntity> entities);
    @Mapping(target = "title", expression = "java(title(request, entity))")
    @Mapping(target = "description", expression = "java(description(request, entity))")
    @Mapping(target = "meditations", source = "request.meditations")
    MeditationAlbumRequest prepareMeditationAlbumRequestFromOldMeditationAlbumEntity(MeditationAlbumRequest request, UserMeditationAlbumEntity entity);
    default String title(MeditationAlbumRequest request, UserMeditationAlbumEntity entity) {
        if (request.getTitle() == null) {
            return entity.getTitle();
        }

        return request.getTitle();
    }
    default String description(MeditationAlbumRequest request, UserMeditationAlbumEntity entity) {
        if (request.getDescription() == null) {
            return entity.getDescription();
        }

        return request.getDescription();
    }
}
