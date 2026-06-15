package com.campus.secondhand.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "campus")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Upload upload = new Upload();
    private Cors cors = new Cors();

    @Data
    public static class Jwt {
        private String secret;
        private long expireHours;
    }

    @Data
    public static class Upload {
        private String path;
        private String urlPrefix;
    }

    @Data
    public static class Cors {
        private String allowedOrigins;
    }
}
