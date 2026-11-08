package com.publicconsult.platform.appointment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface AppointmentMapper {
    List<Map<String,Object>> listForUser(@Param("userId") Long userId);
    List<Map<String,Object>> listForProfessor(@Param("professorId") Long professorId);
    List<Map<String,Object>> listAll();
    int create(@Param("appointmentNo") String appointmentNo, @Param("userId") Long userId,
               @Param("professorId") Long professorId, @Param("type") String type,
               @Param("subject") String subject, @Param("content") String content,
               @Param("time") String time);
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("reply") String reply, @Param("ownerId") Long ownerId,
                     @Param("ownerColumn") String ownerColumn);
    Map<String,Object> findById(@Param("id") Long id);
}
