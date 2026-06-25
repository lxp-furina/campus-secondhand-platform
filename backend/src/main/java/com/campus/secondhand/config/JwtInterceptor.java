package com.campus.secondhand.config;

import com.campus.secondhand.common.Constants;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.utils.AuthContext;
import com.campus.secondhand.utils.AuthUser;
import com.campus.secondhand.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BizException(401, "请先登录");
        }
        Claims claims = jwtUtils.parse(authorization.substring(7));
        Long userId = Long.valueOf(claims.getSubject());
        String role = String.valueOf(claims.get("role"));
        if (path.startsWith("/api/admin") && !Constants.ROLE_ADMIN.equals(role)) {
            throw new BizException(403, "无权访问管理接口");
        }
        AuthContext.set(new AuthUser(userId, role));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/uploads/")
                || path.startsWith("/api/public/")
                || "/api/auth/register".equals(path)
                || "/api/auth/login".equals(path)
                || "/api/auth/admin/login".equals(path);
    }
}
