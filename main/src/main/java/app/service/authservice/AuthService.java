package app.service.authservice;

import app.dto.auth.SignInRequest;
import app.dto.auth.SignUpRequest;
import app.entity.UserEntity;
import app.mapper.UserMapper;
import app.service.JwtService;
import app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String signup(SignUpRequest request) {
        var user = userMapper.signUpDtoToUserEntity(request);
        user.setIsExitButtonPressed(false);
        UserDetails resultUserDetails = userService.createUser(user);

        return jwtService.generateToken(resultUserDetails);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String signin(SignInRequest request) {
        UserEntity resultUser = userService.getUser(request.getEmail());
        resultUser.setIsExitButtonPressed(false);
        resultUser = userService.updateUser(resultUser);

        if (!passwordEncoder.matches(request.getPassword(), resultUser.getPassword())) {
            throw new IllegalArgumentException("password is invalid!");
        }

        return jwtService.generateToken(resultUser);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void logout(UserDetails userDetails) {
        UserEntity resultUser = userService.getUser(userDetails.getUsername());
        resultUser.setIsExitButtonPressed(false);
        userService.updateUser(resultUser);
    }
}
