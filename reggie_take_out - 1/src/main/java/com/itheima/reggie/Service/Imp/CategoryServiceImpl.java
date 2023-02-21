package com.itheima.reggie.Service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.CustomEception;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMappper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMappper, Category> implements CategoryService {
    @Autowired
    CategoryMappper categoryMappper;
    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;
    @Override
    public void update(Long ids) {
        //要判断是否有菜品的

        LambdaQueryWrapper<Dish>    lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(lambdaQueryWrapper);
        if(count>0){
            throw  new CustomEception("已有菜品存在不能删除");
        }
        LambdaQueryWrapper<Setmeal>    lambdaQueryWrapper1 = new LambdaQueryWrapper();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(lambdaQueryWrapper1);

        if(count1>0){
            throw  new CustomEception("已有套餐存在不猛删除");
        }

        LambdaUpdateWrapper<Category> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(Category::getId,ids);
        objectLambdaUpdateWrapper.set(Category::getIsDeleted,1);
        categoryMappper.update(null,objectLambdaUpdateWrapper);
    }
}
