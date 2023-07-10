package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.common.CustomException;
import com.zsx.reggie.entity.Category;
import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.Setmeal;
import com.zsx.reggie.mapper.CategoryMapper;
import com.zsx.reggie.service.CategoryService;
import com.zsx.reggie.service.DishService;
import com.zsx.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //1. 查询当前分类是否关联了菜品，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            //还有关联的菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //2. 查询当前分类是否关联了套餐，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if( count2 >0){
            //还有关联的套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //3. 正常删除分类
        super.removeById(id);
    }
}
