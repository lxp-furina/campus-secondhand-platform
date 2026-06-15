package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.AdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public Result<Views.Dashboard> dashboard() {
        return Result.ok(adminService.dashboard());
    }

    @GetMapping("/users")
    public Result<Page<User>> users(@RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") long current,
                                    @RequestParam(defaultValue = "10") long size) {
        return Result.ok(adminService.users(keyword, current, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @Validated @RequestBody Requests.StatusUpdate request) {
        adminService.updateUserStatus(id, request.getStatus());
        return Result.ok();
    }

    @PutMapping("/items/{id}/off-shelf")
    public Result<Void> adminOffShelf(@PathVariable Long id) {
        adminService.adminOffShelfItem(id);
        return Result.ok();
    }
}
