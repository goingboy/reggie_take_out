package com.zsx.reggie.service;

import com.zsx.reggie.vo.TurnOverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 根据时间区间统计营业额
     * @param beginTime
     * @param endTime
     * @return
     */
    TurnOverReportVO getTurnover(LocalDate beginTime, LocalDate endTime);
}
