package com.zsx.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsx.reggie.dto.OrdersDto;
import com.zsx.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 分页查询订单 同时查询订单详情
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> pageWithOrderDetail(Integer page, Integer pageSize);

}
