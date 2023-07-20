package com.zsx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsx.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    @Select("select sum(amount) from orders where order_time > #{beginTime} and order_time < #{endTime} and status = #{status}")
    Double sumByMap(Map map);
}
