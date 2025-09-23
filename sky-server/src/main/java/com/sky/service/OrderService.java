package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
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

    /**
     * 取消订单
     * @param id
     * @return
     */
    Result cancel(String id);

    /**
     * 再来一单
     * @param id
     * @return
     */
    Result repetition(String id);

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    Result<OrderVO> orderDetails(String id);

    /**
     * 统计各个订单状态
     * @return
     */
    Result<OrderStatisticsVO> orderStatistics();

    /**
     * 接单
     * @param id
     * @return
     */
    Result confirm(long id);

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    Result cancelByAdmin(OrdersCancelDTO ordersCancelDTO);

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    Result rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 派送订单
     * @param id
     * @return
     */
    Result delivery(Long id);

    /**
     * 完成订单
     * @param id
     * @return
     */
    Result complete(Long id);
}
