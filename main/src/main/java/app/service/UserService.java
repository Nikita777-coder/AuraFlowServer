package app.service;

import app.entity.UserEntity;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserService {
//    private final CurrentUserComponent currentUserComponent;
    private final UserRepository userRepository;
//    public UserDetails getCurrentUser() {
//        UserDetails userDetails = currentUserComponent.getCurrentUser();
//
//        return getUser(userDetails.getUsername());
//    }
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
}
