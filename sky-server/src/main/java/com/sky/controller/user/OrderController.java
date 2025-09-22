package com.sky.controller.user;

import com.sky.dto.HistoryOrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation(value = "用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单:{}", ordersSubmitDTO);
        return orderService.submit(ordersSubmitDTO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /***
     *根据id查询订单
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation(value = "根据id查询订单")
    private Result<OrderVO> getById(@PathVariable String id) {
        log.info("根据id查询订单：{}", id);
        return orderService.getById(id);
    }

    /**
     * 查看历史订单
     * @param historyOrdersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation(value = "查看历史订单")
    public Result<PageResult> historyOrders(HistoryOrdersPageQueryDTO historyOrdersPageQueryDTO) {
        log.info("查看历史订单：{}", historyOrdersPageQueryDTO);
        return orderService.historyOrders(historyOrdersPageQueryDTO);
    }
}
