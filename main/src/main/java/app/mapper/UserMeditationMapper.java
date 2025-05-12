package app.mapper;

import app.dto.meditation.UserMeditation;
import app.dto.meditation.UserMeditationUpdateRequest;
import app.entity.usermeditation.UserMeditationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                MeditationMapper.class,
                StatusMapper.class
        }
)
public interface UserMeditationMapper {
    @Mapping(target = "meditation", source = "meditationFromPlatform")
    UserMeditation userMeditationEntityToUserMeditation(UserMeditationEntity userMeditationEntity);
    List<UserMeditation> userMeditationEntitiesToUserMeditations(List<UserMeditationEntity> userMeditationEntities);
    @Mapping(target = "id", source = "userMeditationUpdateRequest.id")
    @Mapping(target = "pauseTime", source = "userMeditationUpdateRequest.pauseTime")
    @Mapping(target = "statuses", ignore = true)
    UserMeditationEntity updateEntity(UserMeditationUpdateRequest userMeditationUpdateRequest, UserMeditationEntity oldEntity);
}
