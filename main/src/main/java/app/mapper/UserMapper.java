package app.mapper;

import app.dto.auth.SignUpRequest;
import app.dto.user.UserData;
import app.dto.user.UserOptions;
import app.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity signUpDtoToUserEntity(SignUpRequest request);
    UserOptions userEntityToUserOptions(UserEntity userEntity);
    UserData userEntityToUserData(UserEntity userEntity);

    @Mapping(target = "id", source = "currentUserOldData.id")
    @Mapping(target = "password", source = "currentUserOldData.password")
    @Mapping(target = "email", source = "currentUserOldData.email")
    @Mapping(target = "role", source = "currentUserOldData.role")
    @Mapping(target = "isPremium", source = "currentUserOldData.isPremium")
    @Mapping(target = "hasPractiseBreathOpt", source = "userData.hasPractiseBreathOpt", defaultExpression = "java(currentUserOldData.getHasPractiseBreathOpt())")
    @Mapping(target = "name", source = "userData.name", defaultExpression = "java(currentUserOldData.getName())")
    @Mapping(target = "hasOpenAppOpt", source = "userData.hasOpenAppOpt", defaultExpression = "java(currentUserOldData.getHasOpenAppOpt())")
    @Mapping(target = "startTimeOfBreathPractise", source = "userData.startTimeOfBreathPractise", defaultExpression = "java(currentUserOldData.getStartTimeOfBreathPractise())")
    @Mapping(target = "stopTimeOfBreathPractise", source = "userData.stopTimeOfBreathPractise", defaultExpression = "java(currentUserOldData.getStopTimeOfBreathPractise())")
    @Mapping(target = "countBreathPractiseReminderPerDay", source = "userData.countBreathPractiseReminderPerDay", defaultExpression = "java(currentUserOldData.getCountBreathPractiseReminderPerDay())")
    UserEntity updateUserData(UserData userData, UserEntity currentUserOldData);
    @AfterMapping
    default void handleNullUserData(@MappingTarget UserEntity userEntity, UserData userData, UserEntity currentUserOldData) {
        if (userData == null && currentUserOldData != null) {
            userEntity.setId(currentUserOldData.getId());
            userEntity.setPassword(currentUserOldData.getPassword());
            userEntity.setEmail(currentUserOldData.getEmail());
            userEntity.setRole(currentUserOldData.getRole());
            userEntity.setIsPremium(currentUserOldData.getIsPremium());
            userEntity.setHasPractiseBreathOpt(currentUserOldData.getHasPractiseBreathOpt());
            userEntity.setName(currentUserOldData.getName());
            userEntity.setHasOpenAppOpt(currentUserOldData.getHasOpenAppOpt());
            userEntity.setStartTimeOfBreathPractise(currentUserOldData.getStartTimeOfBreathPractise());
            userEntity.setStopTimeOfBreathPractise(currentUserOldData.getStopTimeOfBreathPractise());
            userEntity.setCountBreathPractiseReminderPerDay(currentUserOldData.getCountBreathPractiseReminderPerDay());
        }
    }
}
