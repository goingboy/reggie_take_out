package com.zsx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsx.reggie.common.R;
import com.zsx.reggie.dto.OrdersDto;
import com.zsx.reggie.entity.Orders;
import com.zsx.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        log.info("用户下单");

        ordersService.submit(orders);

        return R.success("下单成功");
    }


    /**
     * 分页查询订单数据
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(Integer page, Integer pageSize){
        log.info("分页查询订单");

        Page<OrdersDto> ordersDtoPage = ordersService.pageWithOrderDetail(page, pageSize);

        return R.success(ordersDtoPage);
    }

    /**
     * 分页查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number, String beginTime, String endTime){

        log.info("查询参数 startTime={}, endTime={}", beginTime, endTime);

        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //构造查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        queryWrapper.gt(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.le(endTime != null, Orders::getOrderTime, endTime);

        ordersService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 派送外卖
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> send(@RequestBody Orders orders){

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(orders.getStatus() != null, Orders::getStatus, orders.getStatus());
//        queryWrapper.eq(orders.getId() != null, Orders::getId, orders.getId());

        ordersService.updateById(orders);
        return R.success("派送成功");

    }

}
