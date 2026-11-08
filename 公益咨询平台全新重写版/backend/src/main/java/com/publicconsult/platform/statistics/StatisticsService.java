package com.publicconsult.platform.statistics;

import com.publicconsult.platform.common.BusinessException;
import com.publicconsult.platform.security.AuthUser;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatisticsService {
    private final StatisticsMapper mapper;
    public StatisticsService(StatisticsMapper mapper) { this.mapper = mapper; }
    public Map<String, Object> dashboard(AuthUser user) {
        Map<String, Object> result = new LinkedHashMap<>();
        switch (user.role()) {
            case "USER" -> result.put("summary", mapper.userSummary(user.id()));
            case "PROFESSOR" -> result.put("summary", mapper.professorSummary(user.id()));
            case "ADMIN" -> result.put("summary", mapper.adminSummary());
            default -> throw new BusinessException("无效角色");
        }
        result.put("appointmentStatus", mapper.appointmentStatus(
                "USER".equals(user.role()) ? user.id() : null,
                "PROFESSOR".equals(user.role()) ? user.id() : null));
        result.put("monthlyAppointments", mapper.monthlyAppointments(
                "USER".equals(user.role()) ? user.id() : null,
                "PROFESSOR".equals(user.role()) ? user.id() : null));
        return result;
    }
}
