package com.csu.mypetstore.controller;


import com.alibaba.fastjson2.JSON;
import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.*;
import com.csu.mypetstore.service.AccountService;
import com.csu.mypetstore.service.CatalogService;
import com.csu.mypetstore.service.OrderService;
import org.apache.ibatis.session.SqlSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private AccountService accountService;

    private Map<String,Cart> cartMap = new HashMap<>();

    @GetMapping("/getCart")
    public Result<?> get(@RequestParam String username){
        Cart cart = cartMap.get(username);
        if (cart == null){
            cart = new Cart();
            cartMap.put(username, cart);
        }
        return Result.success(cart);
    }



    @PostMapping("/addToCart/{itemId}/{username}")
    public Result<?> addToCart(@PathVariable("itemId") String itemId,@PathVariable("username") String username){
        Cart cart = cartMap.get(username);
        if (cart == null){
            cart = new Cart();
            cartMap.put(username,cart);
        }
        if (cart.containsItemId(itemId)){
            cart.incrementQuantityByItemId(itemId);
        }else {
            boolean isInStock = false;
            isInStock = catalogService.isItemInStock(itemId);
            Item item = catalogService.getItem(itemId);
            cart.addItem(item, isInStock);
        }
        cartMap.replace(username,cart);
        return Result.success(cart);
    }




    /**
     * 生成新订单
     * @param
     * @param
     * @return
     */
    @GetMapping("/newOrder/{username}")
    public Result<?> newOrder(@PathVariable("username") String username) {
        Cart cart = cartMap.get(username);
        Account account = accountService.getAccount(username);
        Order order = new Order();
        order.initOrder(account,cart);
//        orderService.insertOrder(order);
        return Result.success(order);
    }


    @PostMapping("/confirmOrder")
    public Result<?> confirm(@RequestBody Order order){
        orderService.insertOrder(order);
        return Result.success(order);
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

    @PostMapping("/update/{username}/{itemId}/{qty}")
    public Result<?> updateCart(@PathVariable("username") String username,@PathVariable("itemId") String itemId,@PathVariable("qty") String qty){
        Cart cart = cartMap.get(username);
        int quantity = Integer.parseInt(qty);
        if (quantity == 0){
            cart.removeItemById(itemId);
        }
        cart.setQuantityByItemId(itemId, quantity);
        return Result.success();
    }

    @DeleteMapping("/delete/{username}/{itemId}")
    public Result<?> deleteItem(@PathVariable("username") String username,@PathVariable("itemId") String itemId){
        Cart cart = cartMap.get(username);
        cart.removeItemById(itemId);
        return Result.success();
    }

    @GetMapping("getOrder/{orderId}")
    public Result<?> getOrder(@PathVariable("orderId") int orderId){
        Order order = orderService.getOrder(orderId);
        String username = order.getUsername();
        cartMap.remove(username);
        return Result.success(order);
    }


}
