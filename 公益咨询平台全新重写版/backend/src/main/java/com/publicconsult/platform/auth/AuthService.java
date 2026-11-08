
package com.publicconsult.platform.auth;

import com.publicconsult.platform.common.BusinessException;
import com.publicconsult.platform.security.AuthUser;
import com.publicconsult.platform.security.JwtTokenProvider;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            AuthMapper mapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider
    ) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(
            String username,
            String password,
            String requestedRole
    ) {
        Map<String, Object> user = mapper.findByUsername(username);

        if (user == null ||
                !passwordEncoder.matches(
                        password,
                        String.valueOf(user.get("password"))
                )) {
            throw new BusinessException("账号或密码错误");
        }

        String actualRole = String.valueOf(user.get("role"));

        if (!actualRole.equals(requestedRole)) {
            throw new BusinessException("所选登录身份与账号实际身份不一致");
        }

        if (!"正常".equals(user.get("status"))) {
            throw new BusinessException("账号当前不可登录");
        }

        if ("PROFESSOR".equals(actualRole)
                && !"已通过".equals(user.get("auditStatus"))) {
            throw new BusinessException("教授账号尚未通过管理员审核");
        }

        AuthUser authUser = new AuthUser(
                ((Number) user.get("id")).longValue(),
                username,
                actualRole
        );

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("token", tokenProvider.create(authUser));
        result.put("id", authUser.id());
        result.put("username", username);
        result.put("nickname", user.get("nickname"));
        result.put("role", actualRole);

        return result;
    }

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(
            String username,
            String password,
            String nickname,
            String role
    ) {
        // 只允许普通用户和教授注册
        if (!"USER".equals(role) && !"PROFESSOR".equals(role)) {
            throw new BusinessException("仅支持普通用户或教授注册");
        }

        // 检查用户名是否重复
        if (mapper.findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 组装用户数据
        Map<String, Object> user = new LinkedHashMap<>();

        user.put("username", username);
        user.put("password", passwordEncoder.encode(password));
        user.put("nickname", nickname);
        user.put("role", role);
        user.put("status", "正常");

        // 插入用户，并由 MyBatis 回填自增主键
        int rows = mapper.insertUser(user);

        if (rows != 1) {
            throw new BusinessException("用户注册失败");
        }

        Object generatedId = user.get("id");

        if (!(generatedId instanceof Number)) {
            throw new BusinessException("用户注册失败，未能获取用户编号");
        }

        Long userId = ((Number) generatedId).longValue();

        // 教授注册后，创建待审核的教授资料
        if ("PROFESSOR".equals(role)) {
            mapper.insertProfessorProfile(userId, nickname);
        }
    }

    /**
     * 查询当前登录用户信息
     */
    public Map<String, Object> currentUser(Long id) {
        Map<String, Object> user = mapper.findById(id);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不向前端返回密码
        user.remove("password");

        return user;
    }
}
