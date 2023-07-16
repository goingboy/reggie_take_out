package com.zsx.reggie.utils;

import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.Setmeal;

/**
 * redis key工具类
 */
public class RedisKeyUtil {

    public final static String DISH_EKY_PREFIX = "dish_";
    public final static String SETMEAL_EKY_PREFIX = "dish_";


    /**
     * 获取redis key
     * @param dish
     * @return
     */
    public static String getCategoryDishKey(Dish dish){
        return DISH_EKY_PREFIX + dish.getCategoryId() + "_" + dish.getStatus();
    }

    public static String getCategorySetmealKey(Setmeal setmeal) {
        return SETMEAL_EKY_PREFIX + setmeal.getCategoryId() + "_" + setmeal.getStatus();
    }
}
