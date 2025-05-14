package app.service;

import app.dto.payment.PaymentNotification;
import app.dto.premium.PremiumData;
import app.entity.payment.TransactionStatus;
import app.mapper.PremiumMapper;
import app.repository.PremiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    public Boolean hasPremium(UserDetails userDetails) {
        return !premiumRepository.getCurrentPremium(
             userDetails.getUsername(),
                TransactionStatus.SUCCESS
        ).isEmpty();
    }
    public List<PremiumData> getHistoryOfPremiums(UserDetails userDetails) {
        return premiumMapper.premiumEntitiesToPremiumDatas(premiumRepository.getPremiumEntitiesByUserEmail(
                userDetails.getUsername()
        ));
    }
    public void logPaymentNotification(PaymentNotification paymentNotification) {
        var payment = premiumRepository.findById(paymentNotification.getObject().getId());

        if (payment.isPresent()) {
           var p = payment.get();
            p.setTransactionStatus(TransactionStatus.valueOf(paymentNotification.getObject().getStatus()));
            premiumRepository.save(p);
        }
    }
}
