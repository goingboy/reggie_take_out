package com.zsx.reggie.controller;

import com.zsx.reggie.common.R;
import com.zsx.reggie.service.ReportService;
import com.zsx.reggie.vo.TurnOverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    public R<TurnOverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        return R.success(reportService.getTurnover(begin, end));
    }

    /**
     * echarts前端需要的图标数据格式一般为
     * x轴:"a, b, c, d, e"
     *      |  |  |  |  |
     * y轴:"1, 2, 3, 4, 5"
     * 因此，返给前端的数据使用字符串隔开就行了。注意x和y的每个位置的值是一一对应起来，
     */
}
