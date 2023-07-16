package com.zsx.reggie.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.common.CustomException;
import com.zsx.reggie.dto.SetmealDto;
import com.zsx.reggie.entity.Setmeal;
import com.zsx.reggie.entity.SetmealDish;
import com.zsx.reggie.mapper.SetmealMapper;
import com.zsx.reggie.service.SetmealDishService;
import com.zsx.reggie.service.SetmealService;
import com.zsx.reggie.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 保存套餐，同时保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐信息
        this.save(setmealDto);

        //保存套餐与菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //方式一：批量保存
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

        //方式二：一个一个单独保存
//        for (int i = 0; i < setmealDishes.size(); i++) {
//            setmealDishes.get(i).setSetmealId(setmealDto.getId());
//            setmealDishService.save(setmealDishes.get(i));
//        }

        //清除缓存数据
        String key = RedisKeyUtil.getCategorySetmealKey(setmealDto);
        redisTemplate.delete(key);

    }

    /**
     * 删除套餐setmeal，同时删除关联关系setmeal_dish
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeWithDish(List<Long> ids) {

        //select count(*） from setmeal where id in (1,2,3) and status = 1;
        //先查询套餐状态，确认是否套餐可以删除，
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);

        //如果有的话，则抛出业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //删除套餐
        this.removeByIds(ids);

        //删除关联关系
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(dishLambdaQueryWrapper);

    }

    /**
     * 查询套餐 同时查询套餐关联的菜品
     *
     * @param id
     * @return
     */
    public SetmealDto getWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();

        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);

        //select * from setmeal_dish where setmeal_id = id;
        //构造查询条件
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());

        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    /**
     * 更新套餐信息和关联的菜品信息
     *
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto) {

        //更新套餐基础信息 操作setmeal表
        this.updateById(setmealDto);

        //删除套餐和菜品关联信息 操作setmeal_dish表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        Long setmealId = setmealDto.getId();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        //插入套餐和菜品关联关系 操作setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

        //清除缓存数据
        String key = RedisKeyUtil.getCategorySetmealKey(setmealDto);
        redisTemplate.delete(key);
    }

    /**
     * 查询套餐列表
     *
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> listSetmeal(Setmeal setmeal) {
        //从缓存中拿套餐列表数据，没有的话再从数据库读取
        String key = RedisKeyUtil.getCategorySetmealKey(setmeal);

        List<Setmeal> setmealList = null;

        String setmealListStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(setmealListStr)) {
            setmealList = JSONObject.parseObject(setmealListStr, List.class);
            return setmealList;
        }

        //否则，从数据库读取套餐列表

        //构造查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行查询
        setmealList = this.list(queryWrapper);

        //加入缓存中
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(setmealList), 1, TimeUnit.HOURS);

        return setmealList;
    }
}
