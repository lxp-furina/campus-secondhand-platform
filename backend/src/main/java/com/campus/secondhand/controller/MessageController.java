package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.service.MessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/public/items/{itemId}/messages")
    public Result<List<Views.MessageView>> list(@PathVariable Long itemId) {
        return Result.ok(messageService.listByItem(itemId));
    }

    @PostMapping("/messages")
    public Result<Message> create(@Validated @RequestBody Requests.MessageSave request) {
        return Result.ok(messageService.create(request));
    }
}
