package app.mapper;

import app.dto.SignUpRequest;
import app.entity.UserEntity;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity signUpDtoToUserEntity(SignUpRequest request);
    @BeforeMapping
    default void encodePassword(SignUpRequest request) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        request.setPassword(encoder.encode(request.getPassword()));
    }
}
