package app.service;

import app.dto.user.UpdatePasswordData;
import app.dto.user.UserData;
import app.dto.user.UserOptions;
import app.entity.UserEntity;
import app.entity.userattributes.Role;
import app.extra.ProgramCommons;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProgramCommons programCommons;
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDetails createUser(UserEntity entity) {
        if (userRepository.findByEmail(entity.getEmail()).isPresent()) {
            throw new IllegalArgumentException("user with this email exists");
        }
        entity.setRole(Role.USER);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setIsBlocked(false);
        entity.setIsPremium(false);

        return userRepository.save(entity);
    }
    public UserEntity getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserEntity updateUser(UserEntity updatedUser) {
        return userRepository.save(updatedUser);
    }

//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    void updateUser(UserEntity userEntity) {
//        userRepository.save(userEntity);
//    }

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

    public UserEntity getUserByEmail(String email) {
        Optional<UserEntity> currentUser = userRepository.findByEmail(email);

        if (currentUser.isEmpty()) {
            throw new IllegalArgumentException("user not found!");
        }

        return currentUser.get();
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void blockUser(UserDetails userDetails,
                          String email) {
        programCommons.checkUserRole(userDetails);
        var user = getUserByEmail(email);
        user.setIsBlocked(true);

        userRepository.save(user);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void unlockUser(UserDetails userDetails,
                           String email) {
        programCommons.checkUserRole(userDetails);
        var user = getUserByEmail(email);
        user.setIsBlocked(false);

        userRepository.save(user);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(UserDetails userDetails) {
        userRepository.delete(getUserByEmail(userDetails.getUsername()));
    }
}
