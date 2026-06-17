package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.*;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.*;
import com.campus.secondhand.service.ItemService;
import com.campus.secondhand.utils.AuthContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemImageMapper imageMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;

    public ItemServiceImpl(ItemMapper itemMapper, ItemImageMapper imageMapper, CategoryMapper categoryMapper,
                           UserMapper userMapper, FavoriteMapper favoriteMapper) {
        this.itemMapper = itemMapper;
        this.imageMapper = imageMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public List<Category> categories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
    }

    @Override
    @Transactional
    public Item create(Requests.ItemSave request) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BizException("分类不存在");
        }
        Item item = new Item();
        BeanUtils.copyProperties(request, item);
        item.setSellerId(AuthContext.userId());
        item.setStatus(Constants.ITEM_ON_SALE);
        itemMapper.insert(item);
        saveImages(item.getId(), request.getImageUrls());
        return item;
    }

    @Override
    @Transactional
    public Item update(Long id, Requests.ItemSave request) {
        Item item = requireOwnerItem(id);
        if (!Constants.ITEM_ON_SALE.equals(item.getStatus())) {
            throw new BizException("仅上架中的物品可编辑");
        }
        BeanUtils.copyProperties(request, item);
        itemMapper.updateById(item);
        imageMapper.delete(new LambdaQueryWrapper<ItemImage>().eq(ItemImage::getItemId, id));
        saveImages(id, request.getImageUrls());
        return item;
    }

    @Override
    public void offShelf(Long id) {
        Item item = requireOwnerItem(id);
        item.setStatus(Constants.ITEM_OFF_SHELF);
        itemMapper.updateById(item);
    }

    @Override
    public Page<Views.ItemView> page(Long categoryId, String keyword, String status, long current, long size) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .eq(categoryId != null, Item::getCategoryId, categoryId)
                .eq(StringUtils.hasText(status), Item::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w.like(Item::getTitle, keyword).or().like(Item::getDescription, keyword))
                .orderByDesc(Item::getCreatedAt);
        Page<Item> itemPage = itemMapper.selectPage(new Page<>(current, size), wrapper);
        Page<Views.ItemView> result = new Page<>(current, size, itemPage.getTotal());
        result.setRecords(itemPage.getRecords().stream().map(this::toView).collect(Collectors.toList()));
        return result;
    }

    @Override
    public Views.ItemView detail(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        return toView(item);
    }

    @Override
    public List<Views.ItemView> mine() {
        return itemMapper.selectList(new LambdaQueryWrapper<Item>()
                        .eq(Item::getSellerId, AuthContext.userId())
                        .orderByDesc(Item::getCreatedAt))
                .stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public List<Views.ItemView> favorites() {
        return favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, AuthContext.userId())
                        .orderByDesc(Favorite::getCreatedAt))
                .stream()
                .map(f -> itemMapper.selectById(f.getItemId()))
                .filter(Objects::nonNull)
                .map(this::toView)
                .collect(Collectors.toList());
    }

    @Override
    public void favorite(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null || !Constants.ITEM_ON_SALE.equals(item.getStatus())) {
            throw new BizException("物品不可收藏");
        }
        Long exists = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, AuthContext.userId())
                .eq(Favorite::getItemId, id));
        if (exists == 0) {
            Favorite favorite = new Favorite();
            favorite.setUserId(AuthContext.userId());
            favorite.setItemId(id);
            favoriteMapper.insert(favorite);
        }
    }

    @Override
    public void unfavorite(Long id) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, AuthContext.userId())
                .eq(Favorite::getItemId, id));
    }

    private Item requireOwnerItem(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException(404, "物品不存在");
        }
        if (!item.getSellerId().equals(AuthContext.userId())) {
            throw new BizException(403, "只能操作自己的物品");
        }
        return item;
    }

    private void saveImages(Long itemId, List<String> urls) {
        List<String> safeUrls = urls == null ? Collections.emptyList() : urls;
        for (int i = 0; i < safeUrls.size(); i++) {
            ItemImage image = new ItemImage();
            image.setItemId(itemId);
            image.setImageUrl(safeUrls.get(i));
            image.setSortOrder(i);
            imageMapper.insert(image);
        }
    }

    private Views.ItemView toView(Item item) {
        Views.ItemView view = new Views.ItemView();
        view.setItem(item);
        view.setSeller(userMapper.selectById(item.getSellerId()));
        view.setCategory(categoryMapper.selectById(item.getCategoryId()));
        view.setImages(imageMapper.selectList(new LambdaQueryWrapper<ItemImage>()
                .eq(ItemImage::getItemId, item.getId())
                .orderByAsc(ItemImage::getSortOrder)));
        Long userId = AuthContext.userId();
        if (userId != null) {
            Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, userId)
                    .eq(Favorite::getItemId, item.getId()));
            view.setFavorite(count > 0);
        }
        return view;
    }
}
