package app.controller;

import app.dto.payment.PaymentNotification;
import app.dto.premium.PremiumData;
import app.dto.premium.PremiumPaymentResponse;
import app.entity.payment.TransactionStatus;
import app.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    @GetMapping
    @ResponseBody
    public Boolean hasPremium(@AuthenticationPrincipal UserDetails userDetails) {
        return premiumService.hasPremium(userDetails);
    }

    @PostMapping("/notification/test")
    public void pushUserTestPaymentNotification(@RequestBody PaymentNotification paymentNotification) {
        premiumService.logPaymentNotification(paymentNotification);
    }

    @PostMapping("/notification")
    public void pushUserPaymentNotification(@RequestBody PaymentNotification paymentNotification) {
        premiumService.logPaymentNotification(paymentNotification);
    }

    @PostMapping
    public PremiumPaymentResponse buyPremium(@AuthenticationPrincipal UserDetails userDetails) {
        return premiumService.buyPremium(userDetails);
    }

    @GetMapping("/status-payment")
    public TransactionStatus getTransactionStatus(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam("payment-id") UUID id) {
        return premiumService.getTransactionStatus(userDetails, id);
    }

    @GetMapping("/history")
    @ResponseBody
    public List<PremiumData> getHistoryOfPremiums(@AuthenticationPrincipal UserDetails userDetails) {
        return premiumService.getHistoryOfPremiums(userDetails);
    }
}
