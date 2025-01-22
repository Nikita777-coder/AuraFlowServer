package app.service;

import app.dto.user.UpdatePasswordData;
import app.dto.user.UserData;
import app.dto.user.UserOptions;
import app.entity.UserEntity;
import app.mapper.UserMapper;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDetails createUser(UserEntity entity) {
        if (userRepository.findByEmail(entity.getEmail()).isPresent()) {
            throw new IllegalArgumentException("user with this email exists");
        }

        return userRepository.save(entity);
    }
    public UserDetails getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserOptions getOptions(UserDetails currentUserDetails) {
        return userMapper.userEntityToUserOptions(getUserByEmail(currentUserDetails.getUsername()));
    }

    public UserData getUserData(UserDetails currentUserDetails) {
        return userMapper.userEntityToUserData(getUserByEmail(currentUserDetails.getUsername()));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserData updateUser(UserDetails currentUserDetails,
                               UserData newUserData) {
        UserEntity currentUpdatedUser = userMapper.updateUserData(newUserData, getUserByEmail(currentUserDetails.getUsername()));
        userRepository.save(currentUpdatedUser);
        return userMapper.userEntityToUserData(currentUpdatedUser);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updatePassword(UserDetails currentUserDetails,
                               UpdatePasswordData updatePasswordData) {
        UserEntity currentUser = getUserByEmail(currentUserDetails.getUsername());

        if (!passwordEncoder.matches(updatePasswordData.getOldPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Old password is not correct!");
        }

        currentUser.setPassword(passwordEncoder.encode(updatePasswordData.getNewPassword()));
        userRepository.save(currentUser);
    }

    private UserEntity getUserByEmail(String email) {
        Optional<UserEntity> currentUser = userRepository.findByEmail(email);

        if (currentUser.isEmpty()) {
            throw new RuntimeException("get current user error!");
        }

        return currentUser.get();
    }
}
