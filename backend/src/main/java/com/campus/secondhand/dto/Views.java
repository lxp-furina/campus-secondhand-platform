package com.campus.secondhand.dto;

import com.campus.secondhand.entity.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class Views {
    @Data
    public static class LoginView {
        private String token;
        private User user;
    }

    @Data
    public static class ItemView {
        private Item item;
        private User seller;
        private Category category;
        private List<ItemImage> images;
        private boolean favorite;
    }

    @Data
    public static class OrderView {
        private Order order;
        private Item item;
        private User buyer;
        private User seller;
    }

    @Data
    public static class MessageView {
        private Message message;
        private User user;
        private List<MessageView> replies;
    }

    @Data
    public static class ReportView {
        private Report report;
        private Item item;
        private User reporter;
        private User handler;
    }

    @Data
    public static class Dashboard {
        private long userTotal;
        private long itemTotal;
        private long orderTotal;
        private long pendingReportTotal;
        private Map<String, Long> orderStatus;
    }
}
