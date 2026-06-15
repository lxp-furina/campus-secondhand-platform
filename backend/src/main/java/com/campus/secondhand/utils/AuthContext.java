package com.campus.secondhand.utils;

public final class AuthContext {
    private static final ThreadLocal<AuthUser> LOCAL = new ThreadLocal<>();

    private AuthContext() {}

    public static void set(AuthUser user) {
        LOCAL.set(user);
    }

    public static AuthUser get() {
        return LOCAL.get();
    }

    public static Long userId() {
        AuthUser user = get();
        return user == null ? null : user.getUserId();
    }

    public static String role() {
        AuthUser user = get();
        return user == null ? null : user.getRole();
    }

    public static void clear() {
        LOCAL.remove();
    }
}
