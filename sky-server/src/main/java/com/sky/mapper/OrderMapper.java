package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 保存orders数据
     * @param orders
     */
    void save(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据id查询订单信息
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(String id);

    /**
     * 根据userId查询所有orders(附加条件status查询)
     * @param
     * @return
     */
    Page<Orders> getByUserId(Orders order);

    /**
     * 根据订单id删除订单信息
     * @param id
     */
    @Delete("delete from orders where id = #{id}")
    void deleteByid(String id);

    /**
     * 退单操作
     * @param
     */
    void updateStatus(Orders order);

    /**
     * 根据条件查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询待接单数量
     */
    @Select("select count(*)  from orders where status = 2")
    Integer countToBeConfirmed();

    /**
     * 查询待派送数量
     * @return
     */
    @Select("select count(*)  from orders where status = 3")
    Integer countConfirmed();

    /**
     * 查询派送中数量
     * @return
     */
    @Select("select count(*)  from orders where status = 4")
    Integer countDeliveryInProgress();

    /**
     * 根据状态和下单时间查询订单
     * @param status
     * @param orderTime
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

}
