package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.AuthService;
import com.campus.secondhand.utils.AuthContext;
import com.campus.secondhand.utils.JwtUtils;
import com.campus.secondhand.utils.PasswordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserMapper userMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public Views.LoginView register(Requests.Register request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStudentNo, request.getStudentNo()));
        if (count > 0) {
            throw new BizException("学号已注册");
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(PasswordUtils.sha256(request.getPassword()));
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.USER_ENABLED);
        userMapper.insert(user);
        return buildLogin(user);
    }

    @Override
    public Views.LoginView login(Requests.Login request, boolean adminLogin) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentNo, request.getAccount())
                .or()
                .eq(User::getUsername, request.getAccount())
                .last("limit 1"));
        if (user == null || !PasswordUtils.sha256(request.getPassword()).equals(user.getPassword())) {
            throw new BizException("账号或密码错误");
        }
        if (Constants.USER_DISABLED.equals(user.getStatus())) {
            throw new BizException(403, "账号已被禁用");
        }
        if (adminLogin && !Constants.ROLE_ADMIN.equals(user.getRole())) {
            throw new BizException(403, "非管理员账号");
        }
        return buildLogin(user);
    }

    @Override
    public User profile() {
        return requireUser(AuthContext.userId());
    }

    @Override
    public User updateProfile(Requests.ProfileUpdate request) {
        User user = requireUser(AuthContext.userId());
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public void updatePassword(Requests.PasswordUpdate request) {
        User user = requireUser(AuthContext.userId());
        if (!PasswordUtils.sha256(request.getOldPassword()).equals(user.getPassword())) {
            throw new BizException("旧密码错误");
        }
        user.setPassword(PasswordUtils.sha256(request.getNewPassword()));
        userMapper.updateById(user);
    }

    private Views.LoginView buildLogin(User user) {
        Views.LoginView view = new Views.LoginView();
        view.setToken(jwtUtils.generateToken(user.getId(), user.getRole()));
        user.setPassword(null);
        view.setUser(user);
        return view;
    }

    private User requireUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        if (Constants.USER_DISABLED.equals(user.getStatus())) {
            throw new BizException(403, "账号已被禁用");
        }
        return user;
    }
}
