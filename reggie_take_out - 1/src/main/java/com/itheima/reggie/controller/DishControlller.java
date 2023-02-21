package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dito.DishDto;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.DishFlavorService;
import com.itheima.reggie.Service.DishService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishControlller {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 保存新建菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
//        留在service层去处理
        dishService.savedish(dishDto);
        return R.success("成功了");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize, String name) {
        //第一个开启分页
        Page<Dish> pageInfo = new Page(page, pageSize);
        //第二个构造查询对象
        LambdaQueryWrapper<Dish> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        objectLambdaQueryWrapper.eq(StringUtils.isNotEmpty(name), Dish::getName, name);
        objectLambdaQueryWrapper.orderByDesc(Dish::getCreateTime);
        //调用service的方法
        dishService.page(pageInfo, objectLambdaQueryWrapper);
        //但是少了一个菜品的分类名字
//        我们得需要把他设置进来
        Page<DishDto> dishDtoPage = new Page();
        //使用对象拷贝的功能,给拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        //这个单独出来的里面有我们的数据,所以需要单独的设定
        List<Dish> records = pageInfo.getRecords();
        //创建一个集合来接受这些数据
        List<DishDto> records1 = new ArrayList<DishDto>();
        //循环里面的数据
        for (Dish dish : records) {
            DishDto dishDto = new DishDto();
            //复制属性到对象中
            BeanUtils.copyProperties(dish, dishDto);

            //得到categoryId
            Long categoryId = dish.getCategoryId();
            //查询category的名字
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            records1.add(dishDto);
        }

        //然后把数据加入到
        dishDtoPage.setRecords(records1);

        return R.success(dishDtoPage);

    }

    /**
     *去到修改的页面
     */
    @GetMapping("/{id}")
    public R<DishDto> editDish(@PathVariable Long id) {
        //调用service中的getbyid方法来查询,里面的具体逻辑在service中实现
        DishDto dishDto= dishService.getByIdandpage(id);
        return R.success(dishDto);
    }

    /**
     * 修改操作
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.update(dishDto);
        return R.success("修改成功");

    }
    /**
     *停售方法
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status, Long ids){
        log.info("id:{}",ids);
        //调用方法根据id查询对象
        Dish dish = dishService.getById(ids);
        LambdaUpdateWrapper<Dish> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(Dish::getId, ids);
        if(dish.getStatus()==0){
            objectLambdaUpdateWrapper.set(Dish::getStatus,1);
        }else{
            objectLambdaUpdateWrapper.set(Dish::getStatus,0);
        }
        dishService.update(objectLambdaUpdateWrapper);
        return R.success("停售成功");

    }
    /**
     * 删除方法
     */
    @DeleteMapping()
    public R<String> delete(Long id){
        //主要逻辑就是做逻辑删除
        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        //构造条件
        lambdaUpdateWrapper.eq(Dish::getId,id);
        lambdaUpdateWrapper.set(Dish::getIsDeleted,1);
        //调用service完成查询的条件
        //是否需要完成更好的条件,如果菜品的ID出现在了套餐里面,就不能删除,

        dishService.update(lambdaUpdateWrapper);
        return R.success("删除成功");
    }
    /**
     * 在新增套餐那里可以关联菜品
     */
//    @GetMapping("/list")
//   public R<List<Dish>> getList(Dish dish) {
//        //传过来的是菜品的id,我们要根据菜品的id查询到
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
//        //要注意交互,停售的菜品不能够增加
//        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
//        //排序条件
//        dishLambdaQueryWrapper.orderByDesc(Dish::getCreateTime).orderByDesc(Dish::getStatus);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        return R.success(list);
//
//    }

    /**
     * 为了适配前端页面,所以返回的集合对象的类型有口味
     * @param dish
     * @return
     */


    @GetMapping("/list")
    public R<List<DishDto>> getList(Dish dish) {
        //传过来的是菜品的id,我们要根据菜品的id查询到
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        //要注意交互,停售的菜品不能够增加
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        //排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getCreateTime).orderByDesc(Dish::getStatus);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        //创建一个集合对象,方便返回
        ArrayList<DishDto> dishDtos = new ArrayList<DishDto>();

        for (Dish dish1:list){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            //里面有菜品的名字需要设置
            Long categoryId = dish1.getCategoryId();
            //根据id得到菜品的对象
            Category category = categoryService.getById(categoryId);
            if(dishDto.getCategoryName()==null){
                dishDto.setCategoryName(category.getName());
            }
            //我们需要设置口味的信息
            Long dishid = dish1.getId();
            //根据dishid查询口味的信息
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishid);
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(list1);
            dishDtos.add(dishDto);



        }

        return R.success(dishDtos);

    }
}

