package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.common.BaseContext;
import com.zsx.reggie.common.CustomException;
import com.zsx.reggie.dto.OrdersDto;
import com.zsx.reggie.entity.*;
import com.zsx.reggie.mapper.OrdersMapper;
import com.zsx.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     *
     * @param orders
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(Orders orders) {

        //获得当前用户id
        Long currentUserId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId, currentUserId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户信息
        User user = userService.getById(currentUserId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }
        //生成订单号
        long orderId = IdWorker.getId();

        //生成详细订单数据
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setImage(item.getImage());
            orderDetail.setName(item.getName());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //下单：向订单表插入数据，一条数据
        //设置订单信息
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setUserId(currentUserId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);//待派送

        //设置订单金额
        orders.setAmount(new BigDecimal(amount.get()));

        //设置收货信息
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setPhone(addressBook.getPhone());//收货人手机号
        //收货地址
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        // 向订单明细表插入数据，多条
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(queryWrapper);

    }

    /**
     * 分页查询订单 同时查询订单详情
     *
     * @param page
     * @param pageSize
     */
    @Override
    public Page<OrdersDto> pageWithOrderDetail(Integer page, Integer pageSize) {

        Page<OrdersDto> pageInfo = new Page<>(page, pageSize);
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        Long currentUserId = BaseContext.getCurrentId();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, currentUserId);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        this.page(ordersPage, queryWrapper);

        BeanUtils.copyProperties(ordersPage, pageInfo, "records");

        List<Orders> ordersList = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersList.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            String orderId = item.getNumber();
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper();
            wrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(wrapper);

            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());

        pageInfo.setRecords(ordersDtoList);

        return pageInfo;
    }
}
