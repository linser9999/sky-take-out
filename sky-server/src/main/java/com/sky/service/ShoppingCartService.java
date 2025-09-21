package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    Result add(ShoppingCartDTO shoppingCartDTO);
}
