package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.User;

public interface AdminService {
    Views.Dashboard dashboard();
    Page<User> users(String keyword, long current, long size);
    void updateUserStatus(Long id, String status);
    void adminOffShelfItem(Long id);
}
