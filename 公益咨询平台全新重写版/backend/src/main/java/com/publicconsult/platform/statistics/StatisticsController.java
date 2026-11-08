package com.publicconsult.platform.statistics;

import com.publicconsult.platform.common.ApiResult;
import com.publicconsult.platform.security.AuthContext;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService service;
    public StatisticsController(StatisticsService service) { this.service = service; }
    @GetMapping("/dashboard")
    public ApiResult<Map<String, Object>> dashboard() {
        return ApiResult.ok(service.dashboard(AuthContext.require()));
    }
}
