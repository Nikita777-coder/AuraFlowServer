package app.mapper;

import app.dto.meditation.UserMeditation;
import app.dto.meditation.UserMeditationUpdateRequest;
import app.entity.usermeditation.StatusEntity;
import app.entity.usermeditation.UserMeditationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMeditationMapper {
    UserMeditation userMeditationEntityToUserMeditation(UserMeditationEntity userMeditationEntity);
    List<UserMeditation> userMeditationEntitiesToUserMeditations(List<UserMeditationEntity> userMeditationEntities);
    @Mapping(target = "id", source = "userMeditationUpdateRequest.id")
    @Mapping(target = "statuses", expression = "java(statuses(userMeditationUpdateRequest, oldEntity))")
    @Mapping(target = "pauseTime", source = "userMeditationUpdateRequest.pauseTime")
    UserMeditationEntity updateEntity(UserMeditationUpdateRequest userMeditationUpdateRequest, UserMeditationEntity oldEntity);
    default List<StatusEntity> statuses(UserMeditationUpdateRequest userMeditationUpdateRequest, UserMeditationEntity oldEntity) {
        if (userMeditationUpdateRequest.getStatuses() != null) {
            return userMeditationUpdateRequest.getStatuses();
        }

        return oldEntity.getStatuses();
    }
}
