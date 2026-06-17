package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.entity.Item;

import java.util.List;

public interface ItemService {
    List<Category> categories();
    Item create(Requests.ItemSave request);
    Item update(Long id, Requests.ItemSave request);
    void offShelf(Long id);
    Page<Views.ItemView> page(Long categoryId, String keyword, String status, long current, long size);
    Views.ItemView detail(Long id);
    List<Views.ItemView> mine();
    List<Views.ItemView> favorites();
    void favorite(Long id);
    void unfavorite(Long id);
}
