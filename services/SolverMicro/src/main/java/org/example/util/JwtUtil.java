package org.example.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    /**
     * Получить userId из текущего SecurityContext
     */
    public UUID getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String accountId = jwt.getClaimAsString("accountId");
        return UUID.fromString(accountId);
    }

    /**
     * Получить userId из JWT токена
     */
    public UUID getUserIdFromToken(Jwt jwt) {
        String accountId = jwt.getClaimAsString("accountId");
        return UUID.fromString(accountId);
    }

    /**
     * Получить роли из текущего токена
     */
    public List<String> getCurrentUserRoles() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return jwt.getClaimAsStringList("roles");
    }

    /**
     * Проверить, является ли пользователь администратором
     */
    public boolean isCurrentUserAdmin() {
        List<String> roles = getCurrentUserRoles();
        return roles != null && roles.contains("admin");
    }
}