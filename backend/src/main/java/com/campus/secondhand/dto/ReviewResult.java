package com.campus.secondhand.dto;

import lombok.Data;

@Data
public class ReviewResult {
    private boolean pass;
    private String reason;

    public static ReviewResult pass(String reason) {
        ReviewResult result = new ReviewResult();
        result.pass = true;
        result.reason = reason == null ? "" : reason;
        return result;
    }

    public static ReviewResult reject(String reason) {
        ReviewResult result = new ReviewResult();
        result.pass = false;
        result.reason = reason == null ? "内容不符合平台规范" : reason;
        return result;
    }
}
