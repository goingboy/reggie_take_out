package com.zsx.reggie.utils;

import com.zsx.reggie.entity.Dish;
import com.zsx.reggie.entity.Setmeal;

/**
 * redis key工具类
 */
public class RedisKeyUtil {

    public final static String DISH_KEY_PREFIX = "dish_";
    public final static String SETMEAL_KEY_PREFIX = "setmeal_";


    /**
     * 获取redis key
     * @param dish
     * @return
     */
    public static String getCategoryDishKey(Dish dish){
        return DISH_KEY_PREFIX + dish.getCategoryId() + "_" + dish.getStatus();
    }

    public static String getCategorySetmealKey(Setmeal setmeal) {
        return SETMEAL_KEY_PREFIX + setmeal.getCategoryId() + "_" + setmeal.getStatus();
    }
}
