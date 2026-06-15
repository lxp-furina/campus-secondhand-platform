package com.campus.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class Requests {
    @Data
    public static class Register {
        @NotBlank(message = "学号不能为空")
        private String studentNo;
        @NotBlank(message = "昵称不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码至少6位")
        private String password;
        private String phone;
        private String email;
    }

    @Data
    public static class Login {
        @NotBlank(message = "账号不能为空")
        private String account;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class ProfileUpdate {
        @NotBlank(message = "昵称不能为空")
        private String username;
        private String phone;
        private String email;
        private String avatar;
    }

    @Data
    public static class PasswordUpdate {
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, message = "新密码至少6位")
        private String newPassword;
    }

    @Data
    public static class ItemSave {
        @NotBlank(message = "标题不能为空")
        private String title;
        @NotNull(message = "分类不能为空")
        private Long categoryId;
        @NotBlank(message = "描述不能为空")
        private String description;
        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格必须大于0")
        private BigDecimal price;
        @NotBlank(message = "成色不能为空")
        private String conditionLevel;
        private List<String> imageUrls;
    }

    @Data
    public static class MessageSave {
        @NotNull(message = "物品不能为空")
        private Long itemId;
        private Long parentId;
        @NotBlank(message = "留言内容不能为空")
        private String content;
    }

    @Data
    public static class ReportSave {
        @NotNull(message = "物品不能为空")
        private Long itemId;
        @NotBlank(message = "举报原因不能为空")
        private String reason;
    }

    @Data
    public static class StatusUpdate {
        @NotBlank(message = "状态不能为空")
        private String status;
        private String note;
    }
}
