package com.zsx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsx.reggie.dto.DishDto;
import com.zsx.reggie.entity.Category;
import com.zsx.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
