package com.itheima.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.Dito.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void savedish(DishDto dishDto);

    DishDto getByIdandpage(Long id);

    void update(DishDto dishDto);
}
