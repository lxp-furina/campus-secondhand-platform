package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Views.LoginView> register(@Validated @RequestBody Requests.Register request) {
        return Result.ok(authService.register(request));
    }

    @PostMapping("/login")
    public Result<Views.LoginView> login(@Validated @RequestBody Requests.Login request) {
        return Result.ok(authService.login(request, false));
    }

    @PostMapping("/admin/login")
    public Result<Views.LoginView> adminLogin(@Validated @RequestBody Requests.Login request) {
        return Result.ok(authService.login(request, true));
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        return Result.ok(authService.profile());
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@Validated @RequestBody Requests.ProfileUpdate request) {
        return Result.ok(authService.updateProfile(request));
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody Requests.PasswordUpdate request) {
        authService.updatePassword(request);
        return Result.ok();
    }
}
