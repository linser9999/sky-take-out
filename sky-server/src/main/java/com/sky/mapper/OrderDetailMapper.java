package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 保存order_detail信息
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);
}
