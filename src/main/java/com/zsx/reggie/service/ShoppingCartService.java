package com.zsx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsx.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart saveShoppingCart(ShoppingCart shoppingCart);

    ShoppingCart subShoppingCart(ShoppingCart shoppingCart);
}
