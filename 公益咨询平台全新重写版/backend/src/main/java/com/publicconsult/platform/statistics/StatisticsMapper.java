package com.publicconsult.platform.statistics;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {
    Map<String, Object> userSummary(@Param("userId") Long userId);
    Map<String, Object> professorSummary(@Param("professorId") Long professorId);
    Map<String, Object> adminSummary();
    List<Map<String, Object>> appointmentStatus(@Param("userId") Long userId, @Param("professorId") Long professorId);
    List<Map<String, Object>> monthlyAppointments(@Param("userId") Long userId, @Param("professorId") Long professorId);
}
