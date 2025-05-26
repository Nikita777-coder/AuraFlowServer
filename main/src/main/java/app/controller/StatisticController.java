package app.controller;

import app.dto.statistic.Period;
import app.dto.statistic.Statistic;
import app.dto.statistic.StatisticUpdate;
import app.dto.statistic.UserStatistic;
import app.service.StatisticService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    @PostMapping
    @ResponseBody
    public UUID createUserStatistic(@AuthenticationPrincipal UserDetails userDetails,
                                    @Valid @RequestBody Statistic statistic) {
        return statisticService.create(userDetails, statistic);
    }
    @GetMapping
    @ResponseBody
    public UserStatistic getUserStatistic(@AuthenticationPrincipal UserDetails userDetails,
                                          @Valid @RequestBody Period timePeriod) {
        return statisticService.getUserStatistic(userDetails, timePeriod);
    }
//    @PatchMapping
//    @ResponseBody
//    public Statistic updateUserStatistic(@AuthenticationPrincipal UserDetails userDetails,
//                                         @Valid @RequestBody StatisticUpdate statistic) {
//        return statisticService.updateUserStatistic(statistic);
//    }
}
