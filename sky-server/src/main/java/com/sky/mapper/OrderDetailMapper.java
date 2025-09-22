package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 保存order_detail信息
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /***
     * 根据order_id查询order_detail信息
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(String id);
}
