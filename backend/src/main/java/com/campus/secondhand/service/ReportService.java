package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Report;

public interface ReportService {
    Report create(Requests.ReportSave request);
    Page<Views.ReportView> adminPage(String status, long current, long size);
    Report handle(Long id, String status, String note);
}
