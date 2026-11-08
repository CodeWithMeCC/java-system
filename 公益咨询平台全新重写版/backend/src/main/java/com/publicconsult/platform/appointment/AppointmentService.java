package com.publicconsult.platform.appointment;

import com.publicconsult.platform.common.BusinessException;
import com.publicconsult.platform.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentMapper mapper;
    public AppointmentService(AppointmentMapper mapper) { this.mapper = mapper; }
    public List<Map<String,Object>> list(AuthUser user) {
        return switch (user.role()) {
            case "USER" -> mapper.listForUser(user.id());
            case "PROFESSOR" -> mapper.listForProfessor(user.id());
            case "ADMIN" -> mapper.listAll();
            default -> throw new BusinessException("无权访问预约列表");
        };
    }
    @Transactional
    public void create(AuthUser user, Long professorId, String type, String subject, String content, String time) {
        if (!"USER".equals(user.role())) throw new BusinessException("仅普通用户可以提交预约");
        if (professorId == null || professorId.equals(user.id())) throw new BusinessException("请选择有效教授");
        if (time == null || LocalDateTime.parse(time).isBefore(LocalDateTime.now())) throw new BusinessException("预约时间必须晚于当前时间");
        int inserted = mapper.create("AP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,6).toUpperCase(),
                user.id(), professorId, type, subject, content, time);
        if (inserted == 0) throw new BusinessException("教授不存在或尚未通过审核");
    }
    @Transactional
    public void cancel(AuthUser user, Long id) {
        Map<String,Object> item = mapper.findById(id);
        if (item == null) throw new BusinessException("预约不存在");
        if (!"USER".equals(user.role()) || !user.id().equals(((Number)item.get("userId")).longValue())) throw new BusinessException("无权取消该预约");
        if (!"待确认".equals(item.get("status"))) throw new BusinessException("当前状态不允许取消");
        mapper.updateStatus(id,"已取消",null,user.id(),"user_id");
    }
    @Transactional
    public void decide(AuthUser user, Long id, boolean accept, String reply) {
        if (!"PROFESSOR".equals(user.role())) throw new BusinessException("仅教授可以处理预约");
        Map<String,Object> item=mapper.findById(id);
        if(item==null || !user.id().equals(((Number)item.get("professorId")).longValue())) throw new BusinessException("无权处理该预约");
        if(!"待确认".equals(item.get("status"))) throw new BusinessException("该预约已处理");
        mapper.updateStatus(id,accept?"已接受":"已拒绝",reply,user.id(),"professor_id");
    }
}
