package com.itheima.reggie.Service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Dito.DishDto;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    public void savedish(DishDto dishDto) {
        //目标将过过来的数据保存在两张表里面
        this.save(dishDto);
        Long typeid = dishDto.getId();
        //赋给list集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(typeid);
        }
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 实现查查询数据的参次,把数据传回去
     * @param id
     * @return
     */

    @Override
    public DishDto getByIdandpage(Long id) {
        //首先查询dish的信息
        Dish dish = this.getById(id);
        //然后调用
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        //构造查询条件
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //调用service方法查询
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        //数据的转化,因为需要的数据是Dishdto类型的
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //然后设置一个口味属性
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 显示修改操作
     * @param dishDto
     */
    @Override
    public void update(DishDto dishDto) {
        //主要逻辑先删除,后插入
        this.updateById(dishDto);
        //删除
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //更新
        List<DishFlavor> flavors = dishDto.getFlavors();
        //这个式子表示集合中的对象重新拿出来,然后给他设置了一个dishid
        flavors= flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
