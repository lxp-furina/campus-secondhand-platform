package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.secondhand.common.Constants;
import com.campus.secondhand.dto.Requests;
import com.campus.secondhand.dto.Views;
import com.campus.secondhand.entity.Item;
import com.campus.secondhand.entity.Report;
import com.campus.secondhand.exception.BizException;
import com.campus.secondhand.mapper.ItemMapper;
import com.campus.secondhand.mapper.ReportMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ReportService;
import com.campus.secondhand.utils.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public ReportServiceImpl(ReportMapper reportMapper, ItemMapper itemMapper, UserMapper userMapper) {
        this.reportMapper = reportMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Report create(Requests.ReportSave request) {
        if (itemMapper.selectById(request.getItemId()) == null) {
            throw new BizException("举报物品不存在");
        }
        Report report = new Report();
        report.setItemId(request.getItemId());
        report.setReporterId(AuthContext.userId());
        report.setReason(request.getReason());
        report.setStatus(Constants.REPORT_PENDING);
        reportMapper.insert(report);
        return report;
    }

    @Override
    public Page<Views.ReportView> adminPage(String status, long current, long size) {
        Page<Report> page = reportMapper.selectPage(new Page<>(current, size), new LambdaQueryWrapper<Report>()
                .eq(StringUtils.hasText(status), Report::getStatus, status)
                .orderByDesc(Report::getCreatedAt));
        Page<Views.ReportView> result = new Page<>(current, size, page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toView).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public Report handle(Long id, String status, String note) {
        if (!Arrays.asList(Constants.REPORT_REJECTED, Constants.REPORT_ITEM_REMOVED).contains(status)) {
            throw new BizException("处理状态非法");
        }
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BizException(404, "举报不存在");
        }
        if (!Constants.REPORT_PENDING.equals(report.getStatus())) {
            throw new BizException("举报已处理");
        }
        report.setStatus(status);
        report.setResultNote(note);
        report.setHandlerId(AuthContext.userId());
        reportMapper.updateById(report);
        if (Constants.REPORT_ITEM_REMOVED.equals(status)) {
            Item item = itemMapper.selectById(report.getItemId());
            if (item != null) {
                item.setStatus(Constants.ITEM_OFF_SHELF);
                itemMapper.updateById(item);
            }
        }
        return report;
    }

    private Views.ReportView toView(Report report) {
        Views.ReportView view = new Views.ReportView();
        view.setReport(report);
        view.setItem(itemMapper.selectById(report.getItemId()));
        view.setReporter(userMapper.selectById(report.getReporterId()));
        if (report.getHandlerId() != null) {
            view.setHandler(userMapper.selectById(report.getHandlerId()));
        }
        return view;
    }
}
