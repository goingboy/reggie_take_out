package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.entity.OrderDetail;
import com.zsx.reggie.mapper.OrderDetailMapper;
import com.zsx.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetatiServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
