package app.controller;

import app.dto.statistic.Period;
import app.dto.statistic.Statistic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;
    @PostMapping
    @ResponseBody
    public Statistic createUserStatistic(@Valid @RequestBody Statistic statistic) {
        return statisticService.createNewUserStatistic(statistic);
    }
    @GetMapping
    @ResponseBody
    public Statistic getUserStatistic(@Valid @RequestBody Period timePeriod) {
        return statisticService.getUserStatistic(timePeriod);
    }
    @PatchMapping
    @ResponseBody
    public Statistic updateUserStatistic(@Valid @RequestBody Statistic statistic) {
        return statisticService.updateUserStatistic(statistic);
    }
}
