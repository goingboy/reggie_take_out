package com.zsx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsx.reggie.dto.SetmealDto;
import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐和套餐与菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除关联关系
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 获取套餐信息和关联的菜品信息
     * @param id
     * @return
     */
    SetmealDto getWithDish(Long id);

    /**
     * 更新套餐信息 同时更新关联的菜品信息
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);
}
