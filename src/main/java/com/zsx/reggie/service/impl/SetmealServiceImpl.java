package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.entity.Category;
import com.zsx.reggie.entity.Setmeal;
import com.zsx.reggie.mapper.CategoryMapper;
import com.zsx.reggie.mapper.SetmealMapper;
import com.zsx.reggie.service.CategoryService;
import com.zsx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
