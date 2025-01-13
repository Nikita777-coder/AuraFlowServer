package app.controller;

import app.dto.premium.PremiumData;
import app.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @PostMapping
//    @ResponseBody
//    public PremiumPaymentData buyPremium(@AuthenticationPrincipal UserDetails userDetails) {
//        return premiumService.buyPremium(userDetails);
//    }

    @GetMapping("/history")
    @ResponseBody
    public List<PremiumData> getHistoryOfPremiums(@AuthenticationPrincipal UserDetails userDetails) {
        return premiumService.getHistoryOfPremiums(userDetails);
    }
}
