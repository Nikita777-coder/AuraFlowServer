package app.service;

import app.component.CurrentUserComponent;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserService {
    private final CurrentUserComponent currentUserComponent;
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

    public UserOptions getOptions() {
        return userMapper.userEntityToUserOptions(getCurrentUser());
    }

    public UserData getUserData() {
        return userMapper.userEntityToUserData(getCurrentUser());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserData updateUser(UserData newUserData) {
        UserEntity currentUpdatedUser = userMapper.updateUserData(newUserData, getCurrentUser());
        userRepository.save(currentUpdatedUser);
//        Map<String, Object> currentUserProperties = currentUser.toMap();
//        Map<String, Object> newUserDataProperties = newUserData.toMap();
//        Map<String, Object> propertiesForUpdating = new HashMap<>();
//
//        for (String key: newUserDataProperties.keySet()) {
//            if (currentUserProperties.containsKey(key) && newUserDataProperties.get(key) != null) {
//                propertiesForUpdating.put(key, newUserDataProperties.get(key));
//            }
//        }
//
//        currentUser
        return userMapper.userEntityToUserData(currentUpdatedUser);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updatePassword(UpdatePasswordData updatePasswordData) {
        UserEntity currentUser = getCurrentUser();

        if (!passwordEncoder.matches(updatePasswordData.getOldPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Old password is not correct!");
        }

        currentUser.setPassword(passwordEncoder.encode(updatePasswordData.getNewPassword()));
        userRepository.save(currentUser);
    }

    private UserEntity getCurrentUser() {
        UserDetails userDetails = currentUserComponent.getCurrentUser();
        Optional<UserEntity> currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser.isEmpty()) {
            throw new RuntimeException("get current user error!");
        }

        return currentUser.get();
    }
}
