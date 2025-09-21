package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapping;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public Result add(ShoppingCartDTO shoppingCartDTO) {

        ShoppingCart shoppingCart1 = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);

        // 1.根据shoppingCartDTO查询是否有数据
        List<ShoppingCart> shoppingCart = shoppingCartMapping.select(shoppingCart1);

        // 2.没有数据，插入新数据
        if (shoppingCart.isEmpty()) {
            // 添加数据
            addNewShoppingCart(shoppingCart1);
            return Result.success();
        }

        // 3.有数据，将数据数量 +1
        shoppingCartMapping.plusById(shoppingCart.get(0).getId());

        return Result.success();
    }

    /***
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapping.select(shoppingCart);
        return list;
    }

    /***
     * 添加新购物车
     * @param shoppingCart
     */
    private void addNewShoppingCart(ShoppingCart shoppingCart) {

        // 无论添加菜品还是套餐都要设置的数据
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 添加菜品
        if (shoppingCart.getDishId() != null) {
            // 根据菜品dishId查询菜品信息
            Dish dish = dishMapper.getById(shoppingCart.getDishId());
            // 设置新增购物车信息
            shoppingCart.setName(dish.getName());   // 菜名
            shoppingCart.setAmount(dish.getPrice());    // 金额
            shoppingCart.setImage(dish.getImage()); //菜照片

            // 插入数据库
            shoppingCartMapping.add(shoppingCart);
            return;
        }

        // 2.添加套餐逻辑
        // 根据sermealId查询套餐信息
        Setmeal setmeal = setmealMapper.selectById(shoppingCart.getSetmealId());
        // 设置新增购物车信息
        shoppingCart.setName(setmeal.getName());   // 套餐名
        shoppingCart.setAmount(setmeal.getPrice());    // 金额
        shoppingCart.setImage(setmeal.getImage()); // 套餐照片

        shoppingCartMapping.add(shoppingCart);
    }
}
