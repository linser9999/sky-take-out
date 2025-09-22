package com.sky.service;

import com.sky.dto.HistoryOrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    Result<OrderSubmitVO> submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /***
     *根据id查询订单
     * @param id
     * @return
     */
    Result<OrderVO> getById(String id);

    /**
     * 查看历史订单
     * @param historyOrdersPageQueryDTO
     * @return
     */
    Result<PageResult> historyOrders(HistoryOrdersPageQueryDTO historyOrdersPageQueryDTO);
}
