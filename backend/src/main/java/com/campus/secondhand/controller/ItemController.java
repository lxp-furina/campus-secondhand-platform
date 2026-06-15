package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.entity.Item;
import com.campus.secondhand.service.ItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/public/categories")
    public Result<List<Category>> categories() {
        return Result.ok(itemService.categories());
    }

    @GetMapping("/public/items")
    public Result<Page<Views.ItemView>> publicItems(@RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "1") long current,
                                                    @RequestParam(defaultValue = "10") long size) {
        return Result.ok(itemService.page(categoryId, keyword, Constants.ITEM_ON_SALE, current, size));
    }

    @GetMapping("/public/items/{id}")
    public Result<Views.ItemView> detail(@PathVariable Long id) {
        return Result.ok(itemService.detail(id));
    }

    @GetMapping("/admin/items")
    public Result<Page<Views.ItemView>> adminItems(@RequestParam(required = false) Long categoryId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "1") long current,
                                                   @RequestParam(defaultValue = "10") long size) {
        return Result.ok(itemService.page(categoryId, keyword, status, current, size));
    }

    @PostMapping("/items")
    public Result<Item> create(@Validated @RequestBody Requests.ItemSave request) {
        return Result.ok(itemService.create(request));
    }

    @PutMapping("/items/{id}")
    public Result<Item> update(@PathVariable Long id, @Validated @RequestBody Requests.ItemSave request) {
        return Result.ok(itemService.update(id, request));
    }

    @PutMapping("/items/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        itemService.offShelf(id);
        return Result.ok();
    }

    @GetMapping("/items/mine")
    public Result<List<Views.ItemView>> mine() {
        return Result.ok(itemService.mine());
    }

    @PostMapping("/items/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        itemService.favorite(id);
        return Result.ok();
    }

    @DeleteMapping("/items/{id}/favorite")
    public Result<Void> unfavorite(@PathVariable Long id) {
        itemService.unfavorite(id);
        return Result.ok();
    }
}
