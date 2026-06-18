package com.campus.secondhand.service;

import com.campus.secondhand.config.AppProperties;
import com.campus.secondhand.dto.ReviewResult;
import com.campus.secondhand.exception.BizException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AiReviewService {
    private final AppProperties props;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiReviewService(AppProperties props) {
        this.props = props;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.getAi().getTimeoutMs());
        factory.setReadTimeout(props.getAi().getTimeoutMs());
        this.restTemplate = new RestTemplate(factory);
    }

    public ReviewResult review(String title, String description, String category) {
        if (!props.getAi().isEnabled()) {
            log.warn("[AI审核] 已关闭，商品将直接通过");
            return ReviewResult.pass("AI审核已关闭");
        }
        if (!StringUtils.hasText(props.getAi().getApiKey())) {
            throw new BizException("未配置 AI API Key，请设置 DASHSCOPE_API_KEY 或 application-local.yml");
        }
        long start = System.currentTimeMillis();
        String content = callQwen(buildPrompt(title, description, category));
        ReviewResult result = parseResult(content);
        long elapsed = System.currentTimeMillis() - start;
        log.info("[AI审核] 标题={} | 结果={} | 原因={} | 耗时={}ms | 模型={}",
                title,
                result.isPass() ? "通过" : "拒绝",
                StringUtils.hasText(result.getReason()) ? result.getReason() : "无",
                elapsed,
                props.getAi().getModel());
        return result;
    }

    private String callQwen(String userMessage) {
        String url = props.getAi().getBaseUrl() + "/chat/completions";
        Map<String, Object> body = new HashMap<>();
        body.put("model", props.getAi().getModel());
        body.put("messages", Arrays.asList(
                message("system", "你是校园二手平台内容审核员，只输出 JSON，不要 markdown 或其他文字。"),
                message("user", userMessage)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.getAi().getApiKey());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                throw new BizException("AI审核服务异常：" + error.path("message").asText("未知错误"));
            }
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (BizException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BizException("AI审核服务暂时不可用，请稍后重试");
        } catch (Exception e) {
            throw new BizException("AI审核服务响应解析失败，请稍后重试");
        }
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String buildPrompt(String title, String description, String category) {
        return "请审核以下校园二手商品是否可上架。\n\n"
                + "【规则】\n"
                + "1. 禁止：烟酒、药品、管制器具、色情、赌博、诈骗内容\n"
                + "2. 禁止：留微信/QQ/电话等站外联系方式\n"
                + "3. 禁止：明显虚假或违规广告\n"
                + "4. 允许：正常校园闲置物品\n\n"
                + "【商品信息】\n"
                + "标题：" + title + "\n"
                + "描述：" + description + "\n"
                + "分类：" + (StringUtils.hasText(category) ? category : "未分类") + "\n\n"
                + "【输出格式】只输出 JSON：\n"
                + "{\"pass\": true, \"reason\": \"\"}\n"
                + "或\n"
                + "{\"pass\": false, \"reason\": \"具体原因\"}";
    }

    private ReviewResult parseResult(String content) {
        try {
            String json = content.replaceAll("```json|```", "").trim();
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start >= 0 && end > start) {
                json = json.substring(start, end + 1);
            }
            JsonNode node = objectMapper.readTree(json);
            boolean pass = node.path("pass").asBoolean(false);
            String reason = node.path("reason").asText("");
            return pass ? ReviewResult.pass(reason) : ReviewResult.reject(reason);
        } catch (Exception e) {
            return ReviewResult.reject("AI审核结果无法解析，请修改内容后重试");
        }
    }
}
