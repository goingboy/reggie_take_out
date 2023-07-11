package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.DishFlavor;
import com.zsx.reggie.mapper.DishFlavorMapper;
import com.zsx.reggie.mapper.DishMapper;
import com.zsx.reggie.service.DishFlavorService;
import com.zsx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
