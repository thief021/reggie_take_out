package com.itheima.reggie.controller;

import com.itheima.reggie.Service.OrdersSerice;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    OrdersSerice ordersSerice;
    /**
     * 这是支付的接口
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders  orders){
        //核心逻辑在service层去实现
        ordersSerice.submit(orders);
        return R.success("支付成功");
    }
}
