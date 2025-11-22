package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;

public interface StatisticsService {

    /**
     * 요약 대시보드 통계 조회
     */
    DashboardSummaryDto getDashboardSummary();

}
