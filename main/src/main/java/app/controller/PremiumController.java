package app.controller;

import app.dto.premium.PremiumData;
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

    @PostMapping
    public UUID buyPremium(@AuthenticationPrincipal UserDetails userDetails) {
        throw new RuntimeException();
//        return premiumService.buyPremium(userDetails);
    }

    @GetMapping("/status-payment")
    public TransactionStatus getTransactionStatus(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam("payment-id") UUID id) {
        throw new RuntimeException();
    }

    @GetMapping("/history")
    @ResponseBody
    public List<PremiumData> getHistoryOfPremiums(@AuthenticationPrincipal UserDetails userDetails) {
        return premiumService.getHistoryOfPremiums(userDetails);
    }
}
