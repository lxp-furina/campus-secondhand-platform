package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reports")
public class Report extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long itemId;
    private Long reporterId;
    private Long handlerId;
    private String reason;
    private String resultNote;
    private String status;
}
