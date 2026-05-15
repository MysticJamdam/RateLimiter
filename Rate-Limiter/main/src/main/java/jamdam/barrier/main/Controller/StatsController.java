package jamdam.barrier.main.Controller;

import jamdam.barrier.main.entity.StatsResponse;
import jamdam.barrier.main.services.MetricsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    @Autowired
    private MetricsServices metricsServices;

    @GetMapping("/stats")
    public StatsResponse stats() {
        return new StatsResponse(
                metricsServices.getTotalRequests(),
                metricsServices.getAllowedRequests(),
                metricsServices.getBlockedRequests()
        );
    }
}
