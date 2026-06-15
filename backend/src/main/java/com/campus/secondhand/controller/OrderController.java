package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/item/{itemId}")
    public Result<Order> create(@PathVariable Long itemId) {
        return Result.ok(orderService.create(itemId));
    }

    @PutMapping("/orders/{id}/status")
    public Result<Order> updateStatus(@PathVariable Long id, @Validated @RequestBody Requests.StatusUpdate request) {
        return Result.ok(orderService.updateStatus(id, request.getStatus()));
    }

    @GetMapping("/orders/buyer")
    public Result<List<Views.OrderView>> buyerOrders() {
        return Result.ok(orderService.buyerOrders());
    }

    @GetMapping("/orders/seller")
    public Result<List<Views.OrderView>> sellerOrders() {
        return Result.ok(orderService.sellerOrders());
    }

    @GetMapping("/admin/orders")
    public Result<Page<Views.OrderView>> adminOrders(@RequestParam(required = false) String status,
                                                     @RequestParam(defaultValue = "1") long current,
                                                     @RequestParam(defaultValue = "10") long size) {
        return Result.ok(orderService.adminPage(status, current, size));
    }
}
