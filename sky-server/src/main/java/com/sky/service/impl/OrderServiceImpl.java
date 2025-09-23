package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sky.entity.Orders.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public Result<OrderSubmitVO> submit(OrdersSubmitDTO ordersSubmitDTO) {

        // 1.查询相关地址簿
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        // 该用户地址不存在，抛出异常
        if (BeanUtil.isEmpty(addressBook)) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2.查询购物车信息
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.select(shoppingCart);
        // 购物车数据不存在，返回错误
        if (list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 3.处理orders订单数据
        Orders orders = BeanUtil.copyProperties(ordersSubmitDTO, Orders.class);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());

        // 4.保存orders数据
        orderMapper.save(orders);

        // 5.处理order_detail数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        // 将购物车数据拷贝到订单详情列表
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtil.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId()); // 设置订单ID
            orderDetails.add(orderDetail);
        }

        // 6.保存order_detail信息
        orderDetailMapper.insertBatch(orderDetails);

        // 7.清空购物车
        shoppingCartMapper.clean(userId);

        // 8.包装返回数据OrderSubmitVO
        OrderSubmitVO orderSubmitVO = BeanUtil.copyProperties(orders, OrderSubmitVO.class);
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());

        // 9.返回数据
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        /**
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
         */
        log.info("跳过微信支付，支付成功");

        paySuccess(ordersPaymentDTO.getOrderNumber());
        return new OrderPaymentVO();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /***
     *根据id查询订单
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Result<OrderVO> getById(String id) {

        // 1.根据id查询orders信息
        Orders orders = orderMapper.getById(id);

        // 2.根据order_id查询order_detail信息
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);

        // 封装数据并返回
        OrderVO orderVO = BeanUtil.copyProperties(orders, OrderVO.class);
        if (orderVO != null) {
            orderVO.setOrderDetailList(orderDetails);
        }
        return Result.success(orderVO);
    }

    /**
     * 查看历史订单
     * @param historyOrdersPageQueryDTO
     * @return
     */
    @Override
    @Transactional
    public Result<PageResult> historyOrders(HistoryOrdersPageQueryDTO historyOrdersPageQueryDTO) {

        // 分页
        Page<Object> page = PageHelper.startPage(historyOrdersPageQueryDTO.getPage(), historyOrdersPageQueryDTO.getPageSize());

        // 1.根据userId查询所有orders(附加条件status查询)
        Orders order = new Orders();
        order.setUserId(BaseContext.getCurrentId());
        order.setStatus(historyOrdersPageQueryDTO.getStatus());
        Page<Orders> orderList = orderMapper.getByUserId(order);

        // 如果没有历史记录，返回空
        if (orderList.isEmpty()) {
            return Result.success(new PageResult());
        }

        // 新建返回数据
        Page<OrderVO> orderVOList = new Page<>();

        // 查询每个订单的菜品信息
        for (Orders o : orderList) {
            // 组装订单信息
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(String.valueOf(o.getId()));
            OrderVO orderVO = BeanUtil.copyProperties(o, OrderVO.class);
            orderVO.setOrderDetailList(orderDetailList);
            // 将订单加入orderVOList
            orderVOList.add(orderVO);
        }

        return Result.success(new PageResult(orderVOList.getTotal(), orderVOList.getResult()));
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Result cancel(String id) {
        Orders order = orderMapper.getById(id);

        Integer status = order.getStatus();

        /**
         * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
         *         public static final Integer PENDING_PAYMENT = 1;
         *         public static final Integer TO_BE_CONFIRMED = 2;
         *         public static final Integer CONFIRMED = 3;
         *         public static final Integer DELIVERY_IN_PROGRESS = 4;
         *         public static final Integer COMPLETED = 5;
         *         public static final Integer CANCELLED = 6;
         */
        /**
         * 支付状态 0未支付 1已支付 2退款
     *             public static final Integer UN_PAID = 0;
         *         public static final Integer PAID = 1;
         *         public static final Integer REFUND = 2;
         */

        // 根据不同的订单状态进行不同处理
        // 1.待付款
        if (status.equals(PENDING_PAYMENT)) {
            order.setStatus(CANCELLED);
            orderMapper.updateStatus(order);
        }
        // 2.待接单
        if (status.equals(TO_BE_CONFIRMED)) {
            order.setStatus(CANCELLED);
            order.setPayStatus(REFUND);
            orderMapper.updateStatus(order);
        }
        // 3.已接单,派送中或者已完成
        if (status.equals(CONFIRMED) || status.equals(DELIVERY_IN_PROGRESS) || status.equals(COMPLETED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_ALLOW_CANCEL);
        }

        return Result.success();
    }

    @Override
    public Result repetition(String id) {
        // 1.根据id查询菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 2.加入购物车
        ArrayList<ShoppingCart> list = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            // 转为购物车对象
            ShoppingCart shoppingCart = BeanUtil.copyProperties(orderDetail, ShoppingCart.class);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            list.add(shoppingCart);
        }
        // 插入数据库
        shoppingCartMapper.addBatch(list);

        return Result.success();
    }

    /**
     * 根据条件查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 1.分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        // 2.根据条件查询
        Page<Orders> page = orderMapper.conditionSearch(ordersPageQueryDTO);

        // 3.包装数据并返回
        return Result.success(new PageResult(page.getTotal(), page.getResult()));
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public Result<OrderVO> orderDetails(String id) {
        // 1.根据id查询orders
        Orders orders = orderMapper.getById(id);

        // 2.根据id查询order_detail
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 3.包装数据返回
        OrderVO orderVO = BeanUtil.copyProperties(orders, OrderVO.class);
        orderVO.setOrderDetailList(orderDetailList);

        return Result.success(orderVO);
    }

    /**
     * 统计各个订单状态
     * @return
     */
    @Override
    public Result<OrderStatisticsVO> orderStatistics() {
        // 1.查询待接单数量
        Integer toBeConfirmed =  orderMapper.countToBeConfirmed();

        // 2.查询待派送数量
        Integer confirmed = orderMapper.countConfirmed();

        // 3.查询派送中数量
        Integer deliveryInProgress = orderMapper.countDeliveryInProgress();

        // 4.包装数据
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return Result.success(orderStatisticsVO);
    }

    /**
     * 接单
     * @param id
     * @return
     */
    @Override
    public Result confirm(long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(CONFIRMED);
        orderMapper.updateStatus(orders);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     * @return
     */
    @Override
    public Result cancelByAdmin(OrdersCancelDTO ordersCancelDTO) {
        // 包装数据
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(CANCELLED);    // 取消订单
        orders.setPayStatus(REFUND);    // 退款
        orders.setCancelReason(ordersCancelDTO.getCancelReason());

        orderMapper.update(orders);
        return Result.success();
    }

    /**
     * 拒绝订单
     * @param ordersRejectionDTO
     * @return
     */
    @Override
    public Result rejection(OrdersRejectionDTO ordersRejectionDTO) {
        // 包装数据
        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());     // 拒绝订单
        orders.setStatus(CANCELLED);    // 取消订单
        orders.setPayStatus(REFUND);    // 退款

        orderMapper.update(orders);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id
     * @return
     */
    @Override
    public Result delivery(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id
     * @return
     */
    @Override
    public Result complete(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(COMPLETED);
        orderMapper.update(orders);
        return Result.success();
    }
}
