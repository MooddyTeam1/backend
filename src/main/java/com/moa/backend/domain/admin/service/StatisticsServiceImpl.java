package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.statistics.dashboard.DashboardSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        return null;
    }

}
