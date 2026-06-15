package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Order;

import java.util.List;

public interface OrderService {
    Order create(Long itemId);
    Order updateStatus(Long id, String status);
    List<Views.OrderView> buyerOrders();
    List<Views.OrderView> sellerOrders();
    Page<Views.OrderView> adminPage(String status, long current, long size);
}
