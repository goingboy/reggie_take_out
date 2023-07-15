package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.dto.DishDto;
import com.zsx.reggie.entity.Category;
import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.DishFlavor;
import com.zsx.reggie.mapper.DishMapper;
import com.zsx.reggie.service.CategoryService;
import com.zsx.reggie.service.DishFlavorService;
import com.zsx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;

    /**
     * 保存菜品，同时保存菜品口味
     * @param dishDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品基本信息 到表dish
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        //需要给每个口味数据来设置菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map( (item) -> {
                item.setDishId(dishId);
                    return item;
                }
        ).collect(Collectors.toList());

        //保存口味信息 到表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品信息，同时查询出口味信息 查两个表dish、dish_flavor
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     * @param dishDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(DishDto dishDto) {
        //1.更新菜品基本信息
        this.updateById(dishDto);

        //2.清理当前菜品的已有的口味数据 dish_flavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //3.再提交当前菜品的口味数据 dish_flavor的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map( (item) -> {
                    item.setDishId(dishDto.getId());
                    return item;
                }
        ).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 查询菜品列表，同时也查询出这些菜品的口味信息
     * @param dish
     * @return
     */
    public List<DishDto> listWithFlavor(Dish dish) {

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //增加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //只查询状态为1（起售）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        List<Dish> dishList = this.list(queryWrapper);

        //查询菜品的分类和口味信息
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //拷贝属性
            BeanUtils.copyProperties(item, dishDto);

            //设置分类名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            //设置口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }
}
