package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Result;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Report;
import com.campus.secondhand.service.ReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/reports")
    public Result<Report> create(@Validated @RequestBody Requests.ReportSave request) {
        return Result.ok(reportService.create(request));
    }

    @GetMapping("/admin/reports")
    public Result<Page<Views.ReportView>> adminReports(@RequestParam(required = false) String status,
                                                       @RequestParam(defaultValue = "1") long current,
                                                       @RequestParam(defaultValue = "10") long size) {
        return Result.ok(reportService.adminPage(status, current, size));
    }

    @PutMapping("/admin/reports/{id}/handle")
    public Result<Report> handle(@PathVariable Long id, @Validated @RequestBody Requests.StatusUpdate request) {
        return Result.ok(reportService.handle(id, request.getStatus(), request.getNote()));
    }
}
