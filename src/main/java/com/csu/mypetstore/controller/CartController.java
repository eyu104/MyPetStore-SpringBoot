package com.csu.mypetstore.controller;


import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.Account;
import com.csu.mypetstore.domain.Cart;
import com.csu.mypetstore.domain.Order;
import com.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交新订单
     * @param cart
     * @param account
     * @return
     */
    @PostMapping("/newOrder")
    public Result<?> newOrder(@RequestBody Cart cart, @RequestBody Account account) {
        Order order = new Order();
        order.initOrder(account,cart);
        orderService.insertOrder(order);
        return Result.success();
    }

    /**
     * 查看我的订单
     * @param account
     * @return
     */
    @GetMapping("/orderList")
    public Result<?> getOrderList(@RequestBody Account account) {
        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());
        return Result.success(orderList);
    }

}
