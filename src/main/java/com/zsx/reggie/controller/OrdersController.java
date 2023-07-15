package com.zsx.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsx.reggie.common.R;
import com.zsx.reggie.dto.OrdersDto;
import com.zsx.reggie.entity.Orders;
import com.zsx.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
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
}
