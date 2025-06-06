package app.service;

import app.dto.payment.PaymentNotification;
import app.dto.premium.PremiumData;
import app.dto.premium.PremiumIntegrationServiceResponse;
import app.dto.premium.PremiumPaymentResponse;
import app.entity.UserEntity;
import app.entity.payment.PremiumEntity;
import app.entity.payment.TransactionStatus;
import app.mapper.PremiumMapper;
import app.repository.PremiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final WebClientRestService webClientRestService;
    private final UserService userService;

    @Value("${server.integration.payment-service-path}")
    private String paymentServicePath;
    @Value("${server.integration.base-url}")
    private String integrationBaseUrl;
    public Boolean hasPremium(UserDetails userDetails) {
        premiumRepository.findAll().forEach(System.out::println);
        return !premiumRepository.getCurrentPremium(
                userDetails.getUsername(),
                TransactionStatus.SUCCEEDED
        ).isEmpty();
    }
    public List<PremiumData> getHistoryOfPremiums(UserDetails userDetails) {
        return premiumMapper.premiumEntitiesToPremiumDatas(premiumRepository.getPremiumEntitiesByUserEntity_Email(
                userDetails.getUsername()
        ));
    }
    public void logPaymentNotification(PaymentNotification paymentNotification) {
        var payment = premiumRepository.findById(paymentNotification.getObject().getId());

        if (payment.isPresent()) {
            var p = payment.get();
            p.setTransactionStatus(TransactionStatus.valueOf(paymentNotification.getObject().getStatus().toUpperCase()));

            if (p.getTransactionStatus() == TransactionStatus.SUCCEEDED) {
                p.setExpiredTime(LocalDateTime.now().plusDays(30));
                p.getUserEntity().setIsPremium(true);
            }

            premiumRepository.save(p);
        }
    }
    public PremiumPaymentResponse buyPremium(UserDetails userDetails) {
        var ans = webClientRestService.post(
                integrationBaseUrl,
                paymentServicePath,
                PremiumIntegrationServiceResponse.class
        );

        UserEntity userEntity = userService.getUserByEmail(userDetails.getUsername());
        PremiumEntity premiumEntity = premiumMapper.premiumIntegrationServiceResponseToPremiumEntity(ans);
        premiumEntity.setUserEntity(userEntity);
        premiumEntity.setTransactionTime(LocalDateTime.now());

        PremiumPaymentResponse response = new PremiumPaymentResponse();
        response.setId(premiumRepository.save(premiumEntity).getId());
        response.setPayToken(premiumEntity.getPaymentToken());

        return response;
    }
    public TransactionStatus getTransactionStatus(UserDetails userDetails, UUID paymentId) {
        return premiumRepository.getPremiumEntityByIdAndUserEntity_Email(paymentId, userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("not found"))
                .getTransactionStatus();
    }
}
