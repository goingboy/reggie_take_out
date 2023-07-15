package com.zsx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsx.reggie.common.BaseContext;
import com.zsx.reggie.common.R;
import com.zsx.reggie.entity.ShoppingCart;
import com.zsx.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 */

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车：{}", shoppingCart);

        ShoppingCart savedCart = shoppingCartService.saveShoppingCart(shoppingCart);

        //如果不存在，设置数量为1
        return R.success(savedCart);
    }

    /**
     * 删减购物车商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车：{}", shoppingCart);

        ShoppingCart savedCart = shoppingCartService.subShoppingCart(shoppingCart);

        return R.success(savedCart);
    }


    /**
     * 查询购物车内的商品
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        log.info("查询购物车的商品列表");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){

        log.info("清空购物车的商品列表");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }

}
