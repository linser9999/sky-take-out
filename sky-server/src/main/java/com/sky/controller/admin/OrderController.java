package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单模块接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation(value = "订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("根据条件查询订单：{}", ordersPageQueryDTO);
        return orderService.conditionSearch(ordersPageQueryDTO);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation(value = "查询订单详情")
    public Result<OrderVO> orderDetails(@PathVariable String id) {
        log.info("查询订单详情：{}", id);
        return orderService.orderDetails(id);
    }

    /**
     * 统计各个订单状态
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation(value = "统计各个订单状态")
    public Result<OrderStatisticsVO> orderStatistics() {
        log.info("统计各个订单状态");
        return orderService.orderStatistics();
    }

    /**
     * 接单
     * @param ordersDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation(value = "接单")
    public Result confirm(@RequestBody OrdersDTO ordersDTO) {
        log.info("接单：{}", ordersDTO.getId());
        return orderService.confirm(ordersDTO.getId());
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation(value = "取消订单")
    public Result cacel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单");
        return orderService.cancelByAdmin(ordersCancelDTO);
    }

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation(value = "拒绝订单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒绝订单：{}", ordersRejectionDTO);
        return orderService.rejection(ordersRejectionDTO);
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation(value = "派送订单")
    public Result delivery(@PathVariable Long id) {
        log.info("派送订单:{}", id);
        return orderService.delivery(id);
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation(value = "完成订单")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单:{}", id);
        return orderService.complete(id);
    }
}
