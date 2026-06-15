package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Item;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.ItemMapper;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.OrderService;
import com.campus.secondhand.utils.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public OrderServiceImpl(OrderMapper orderMapper, ItemMapper itemMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Order create(Long itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null || !Constants.ITEM_ON_SALE.equals(item.getStatus())) {
            throw new BizException("物品不可下单");
        }
        if (item.getSellerId().equals(AuthContext.userId())) {
            throw new BizException("不能购买自己发布的物品");
        }
        Long exists = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                .eq(Order::getItemId, itemId)
                .ne(Order::getStatus, Constants.ORDER_CANCELLED));
        if (exists > 0) {
            throw new BizException("该物品已有未取消订单");
        }
        Order order = new Order();
        order.setOrderNo("O" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + AuthContext.userId());
        order.setItemId(item.getId());
        order.setBuyerId(AuthContext.userId());
        order.setSellerId(item.getSellerId());
        order.setAmount(item.getPrice());
        order.setStatus(Constants.ORDER_PENDING);
        orderMapper.insert(order);
        return order;
    }

    @Override
    @Transactional
    public Order updateStatus(Long id, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        Long userId = AuthContext.userId();
        boolean isBuyer = order.getBuyerId().equals(userId);
        boolean isSeller = order.getSellerId().equals(userId);
        boolean isAdmin = Constants.ROLE_ADMIN.equals(AuthContext.role());
        if (!isBuyer && !isSeller && !isAdmin) {
            throw new BizException(403, "无权操作订单");
        }
        validateTransition(order, status, isBuyer, isSeller, isAdmin);
        order.setStatus(status);
        orderMapper.updateById(order);
        // 完成交易后物品状态同步为已售出，取消订单时恢复上架。
        Item item = itemMapper.selectById(order.getItemId());
        if (item != null) {
            if (Constants.ORDER_COMPLETED.equals(status)) {
                item.setStatus(Constants.ITEM_SOLD);
            } else if (Constants.ORDER_CANCELLED.equals(status) && Constants.ITEM_SOLD.equals(item.getStatus())) {
                item.setStatus(Constants.ITEM_ON_SALE);
            }
            itemMapper.updateById(item);
        }
        return order;
    }

    @Override
    public List<Views.OrderView> buyerOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                        .eq(Order::getBuyerId, AuthContext.userId())
                        .orderByDesc(Order::getCreatedAt))
                .stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public List<Views.OrderView> sellerOrders() {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                        .eq(Order::getSellerId, AuthContext.userId())
                        .orderByDesc(Order::getCreatedAt))
                .stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public Page<Views.OrderView> adminPage(String status, long current, long size) {
        Page<Order> page = orderMapper.selectPage(new Page<>(current, size), new LambdaQueryWrapper<Order>()
                .eq(StringUtils.hasText(status), Order::getStatus, status)
                .orderByDesc(Order::getCreatedAt));
        Page<Views.OrderView> result = new Page<>(current, size, page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toView).collect(Collectors.toList()));
        return result;
    }

    private void validateTransition(Order order, String next, boolean isBuyer, boolean isSeller, boolean isAdmin) {
        List<String> all = Arrays.asList(Constants.ORDER_PENDING, Constants.ORDER_PROCESSING, Constants.ORDER_COMPLETED, Constants.ORDER_CANCELLED);
        if (!all.contains(next)) {
            throw new BizException("订单状态非法");
        }
        String current = order.getStatus();
        if (isAdmin) {
            return;
        }
        if (Constants.ORDER_PENDING.equals(current) && Constants.ORDER_PROCESSING.equals(next) && isSeller) {
            return;
        }
        if ((Constants.ORDER_PENDING.equals(current) || Constants.ORDER_PROCESSING.equals(current))
                && Constants.ORDER_CANCELLED.equals(next) && (isBuyer || isSeller)) {
            return;
        }
        if (Constants.ORDER_PROCESSING.equals(current) && Constants.ORDER_COMPLETED.equals(next) && isBuyer) {
            return;
        }
        throw new BizException("当前状态不允许该操作");
    }

    private Views.OrderView toView(Order order) {
        Views.OrderView view = new Views.OrderView();
        view.setOrder(order);
        view.setItem(itemMapper.selectById(order.getItemId()));
        view.setBuyer(userMapper.selectById(order.getBuyerId()));
        view.setSeller(userMapper.selectById(order.getSellerId()));
        return view;
    }
}
