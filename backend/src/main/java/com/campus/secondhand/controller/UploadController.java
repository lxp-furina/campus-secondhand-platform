package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.config.AppProperties;
import com.campus.secondhand.exception.BizException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final AppProperties properties;

    public UploadController(AppProperties properties) {
        this.properties = properties;
    }

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if (file.isEmpty()) {
            throw new BizException("文件不能为空");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String suffix = "";
        int index = original.lastIndexOf('.');
        if (index >= 0) {
            suffix = original.substring(index);
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
        Path dir = Paths.get(properties.getUpload().getPath()).toAbsolutePath();
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String url = base + properties.getUpload().getUrlPrefix() + filename;
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.ok(data);
    }
}
