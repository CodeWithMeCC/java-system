package com.publicconsult.platform.auth;

import com.publicconsult.platform.common.ApiResult;
import com.publicconsult.platform.security.AuthContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }

    public record LoginRequest(@NotBlank String username, @NotBlank String password,
                                @Pattern(regexp = "USER|PROFESSOR|ADMIN") String role) { }
    public record RegisterRequest(@NotBlank @Size(min = 4, max = 30) String username,
                                  @NotBlank @Size(min = 8, max = 64) String password,
                                  @NotBlank String nickname,
                                  @Pattern(regexp = "USER|PROFESSOR") String role) { }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody @Validated LoginRequest request) {
        return ApiResult.ok(service.login(request.username(), request.password(), request.role()));
    }
    @PostMapping("/register")
    public ApiResult<Void> register(@RequestBody @Validated RegisterRequest request) {
        service.register(request.username(), request.password(), request.nickname(), request.role());
        return ApiResult.ok("注册成功，教授账号需等待审核", null);
    }
    @GetMapping("/me")
    public ApiResult<Map<String, Object>> me() {
        return ApiResult.ok(service.currentUser(AuthContext.require().id()));
    }
}
