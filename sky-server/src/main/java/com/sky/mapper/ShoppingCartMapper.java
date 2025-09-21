package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据shoppingCartDTO查询是否有数据
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> select(ShoppingCart shoppingCart);

    /**
     * 新增购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time, number)" +
            " values " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime},#{number})")
    void add(ShoppingCart shoppingCart);

    /***
     * 增加数据库number
     * @param id
     */
    @Update("update shopping_cart set number = number + 1 where id = #{id}")
    void plusById(Long id);
}
