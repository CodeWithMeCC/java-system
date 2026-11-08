package com.publicconsult.platform.appointment;

import com.publicconsult.platform.common.ApiResult;
import com.publicconsult.platform.security.AuthContext;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;
    public AppointmentController(AppointmentService service) { this.service=service; }
    public record CreateRequest(@NotNull Long professorId,@NotBlank String type,@NotBlank String subject,@NotBlank String content,@NotBlank String time) { }
    public record DecisionRequest(boolean accept,String reply) { }
    @GetMapping public ApiResult<List<Map<String,Object>>> list(){return ApiResult.ok(service.list(AuthContext.require()));}
    @PostMapping public ApiResult<Void> create(@RequestBody @Valid CreateRequest request){service.create(AuthContext.require(),request.professorId(),request.type(),request.subject(),request.content(),request.time());return ApiResult.ok(null);}
    @PostMapping("/{id}/cancel") public ApiResult<Void> cancel(@PathVariable Long id){service.cancel(AuthContext.require(),id);return ApiResult.ok(null);}
    @PostMapping("/{id}/decision") public ApiResult<Void> decision(@PathVariable Long id,@RequestBody DecisionRequest request){service.decide(AuthContext.require(),id,request.accept(),request.reply());return ApiResult.ok(null);}
}
