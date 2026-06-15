package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("item_images")
public class ItemImage extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private String imageUrl;
    private Integer sortOrder;
}
