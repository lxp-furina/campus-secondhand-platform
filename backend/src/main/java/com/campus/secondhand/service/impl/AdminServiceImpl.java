package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Item;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.entity.Report;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.ItemMapper;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.ReportMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final OrderMapper orderMapper;
    private final ReportMapper reportMapper;

    public AdminServiceImpl(UserMapper userMapper, ItemMapper itemMapper, OrderMapper orderMapper, ReportMapper reportMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
        this.orderMapper = orderMapper;
        this.reportMapper = reportMapper;
    }

    @Override
    public Views.Dashboard dashboard() {
        Views.Dashboard dashboard = new Views.Dashboard();
        dashboard.setUserTotal(userMapper.selectCount(null));
        dashboard.setItemTotal(itemMapper.selectCount(null));
        dashboard.setOrderTotal(orderMapper.selectCount(null));
        dashboard.setPendingReportTotal(reportMapper.selectCount(new LambdaQueryWrapper<Report>()
                .eq(Report::getStatus, Constants.REPORT_PENDING)));
        Map<String, Long> status = new HashMap<>();
        for (String s : new String[]{Constants.ORDER_PENDING, Constants.ORDER_PROCESSING, Constants.ORDER_COMPLETED, Constants.ORDER_CANCELLED}) {
            status.put(s, orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, s)));
        }
        dashboard.setOrderStatus(status);
        return dashboard;
    }

    @Override
    public Page<User> users(String keyword, long current, long size) {
        return userMapper.selectPage(new Page<>(current, size), new LambdaQueryWrapper<User>()
                .and(StringUtils.hasText(keyword), w -> w.like(User::getStudentNo, keyword).or().like(User::getUsername, keyword))
                .orderByDesc(User::getCreatedAt));
    }

    @Override
    public void updateUserStatus(Long id, String status) {
        if (!Constants.USER_ENABLED.equals(status) && !Constants.USER_DISABLED.equals(status)) {
            throw new BizException("用户状态非法");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        if (Constants.ROLE_ADMIN.equals(user.getRole())) {
            throw new BizException("不能禁用管理员账号");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void adminOffShelfItem(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        item.setStatus(Constants.ITEM_OFF_SHELF);
        itemMapper.updateById(item);
    }
}
