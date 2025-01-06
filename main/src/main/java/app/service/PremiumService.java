package app.service;

import app.dto.premium.PremiumData;
import app.entity.UserEntity;
import app.mapper.PremiumMapper;
import app.repository.PremiumRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserService userService;
    private final PremiumMapper premiumMapper;
    public Boolean hasPremium(UserDetails userDetails) {
        return !premiumRepository.getCurrentPremium(
             userService.getCurrentUserEntity(userDetails).getId()
        ).isEmpty();
    }
    public List<PremiumData> getHistoryOfPremiums(UserDetails userDetails) {
        return premiumMapper.premiumEntitiesToPremiumDatas(premiumRepository.getPremiumEntitiesByUserId(
                userService.getCurrentUserEntity(userDetails).getId()
        ));
    }
}