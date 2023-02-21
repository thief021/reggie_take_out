package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.Service.ShoppingCartService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.ThreadLocalImpi;
import com.itheima.reggie.entity.ShoppingCart;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;
    /**
     * 网购物车里面增加物品
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //确定返回的数据是一个shopingcart对象
        //确定购物车的逻辑:点击加号的时候就需要把菜品或者套餐给家进入,然后为了去吧是哪个的用户下的单需要把用户ID给设置进去
        //得到用户的id
        Long userId = ThreadLocalImpi.get();
        shoppingCart.setUserId(userId);
        //根据usrid查询是否有菜品或者套餐信息
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        if(dishId != null){
//            表示确实是有dishID的,表示我们需要把菜品加入到购物车
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);


        }else{
//            如果如果传过来的数据是套餐,需要把套餐加入到购物车
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //把条件传过去
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(shoppingCartOne==null){

            //表示没有查询到,做插入的操作
//            第一次的时候需要设置数目,默认唯一
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartOne= shoppingCart;
        }else {
            //表示有数据做新增的操作
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }

        return R.success(shoppingCartOne);
    }
/**
 * 这是查询购物车的接口
 */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //这个逻辑就是查询的逻辑
        Long aLong = ThreadLocalImpi.get();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,aLong );
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(shoppingCarts);

    }
    /**
     * 删除购物车的菜品
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        //传过来的数据是dishid或者steamlid,所以我们需要先判断是dishid还是stemlid,然后移除stemalID和userID的组合

        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //得到userid
        Long aLong = ThreadLocalImpi.get();
        shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getUserId,aLong);


        //判断是否是setmealId
        Long setmealId = shoppingCart.getSetmealId();

        if (setmealId!=null){
//            表示移除的应该是套餐
            shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            shoppingCartService.remove(shoppingCartLambdaUpdateWrapper);
        }else{
            shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            shoppingCartService.remove(shoppingCartLambdaUpdateWrapper);
        }
        return R.success("删除成功");
    }
    /**
     * 购物车清楚方法
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //根据userid清楚所有的菜品信息
        //得到userid
        Long useId = ThreadLocalImpi.get();
        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getUserId,useId);
        //调用service
        shoppingCartService.remove(shoppingCartLambdaUpdateWrapper);
        return R.success("清楚成功");




    }
}
