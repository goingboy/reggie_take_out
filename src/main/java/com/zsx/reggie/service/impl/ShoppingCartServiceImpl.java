package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.common.BaseContext;
import com.zsx.reggie.common.CustomException;
import com.zsx.reggie.entity.ShoppingCart;
import com.zsx.reggie.mapper.ShoppingCartMapper;
import com.zsx.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加菜品或者套餐到购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart) {

        //设置用户id,指定当前购物车是哪个用户的
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询当前菜品或者套餐是否已经在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if( dishId != null ){
            //如果是菜品，则查询菜品是否在购物车中
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = this.getOne(queryWrapper);

        //如果已存在，则在原来的基础上+1
        if(cart != null){
            Integer number = cart.getNumber();
            cart.setNumber(number+1);
            this.updateById(cart);
        }else{
            //如果不存在，则添加到购物车中
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            cart = shoppingCart;
        }

        return cart;
    }

    /**
     * 删减购物车商品
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart subShoppingCart(ShoppingCart shoppingCart) {

        //设置用户id,指定当前购物车是哪个用户的
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询当前菜品或者套餐是否已经在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if( dishId != null ){
            //如果是菜品，则查询菜品是否在购物车中
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = this.getOne(queryWrapper);

        //如果不存在，则抛出异常
        if(cart == null){
            throw new CustomException("购物车中没有此商品");
        }

        //如果已存在，则在原来的基础上-1，如果是只有一份，则删除
        Integer number = cart.getNumber();
        if(number>1){
            //如果大于1份，则减一
            cart.setNumber(number-1);
            this.updateById(cart);
        }else{
            //如果只有一份，则删除
            cart.setNumber(0);
            this.removeById(cart);
        }
        return cart;

    }
}
