package app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    @GetMapping
    @ResponseBody
    // Get current time in repository for resolving current premium
    public Boolean hasPremium(@RequestParam("id") UUID userId) {
        return premiumService.hasPremium(userId);
    }

    @PostMapping
    @ResponseBody
    public PremiumPaymentData buyPremium(@RequestBody UUID userId) {
        return premiumService.buyPremium(userId);
    }

    @GetMapping("/history")
    @ResponseBody
    public List<PremiumData> getHistoryOfPremiums(@RequestParam("id") UUID userId) {
        return premiumService.getHistoryOfPremiums(userId);
    }
}
