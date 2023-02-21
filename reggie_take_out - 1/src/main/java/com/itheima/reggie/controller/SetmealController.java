package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dito.DishDto;
import com.itheima.reggie.Dito.SetmealDto;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.Service.SetmealDishService;
import com.itheima.reggie.Service.SetmealService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    @Resource
    SetmealService setmealSerivce;
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    CategoryService categoryService;

    /**
     * 这个是分页查查询数据,记得使用了mybatisplus的插件
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Long page,Long pageSize,String name){
        //开启分页
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        //构造查询对象
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件
        setmealLambdaQueryWrapper.eq(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getCreateTime);
        //调用service返回数据
        setmealSerivce.page(pageInfo, setmealLambdaQueryWrapper);
        //数据有一个没有写进去
        Page<SetmealDto> setmealDtoPage = new Page<SetmealDto>();
        BeanUtils.copyProperties(pageInfo, setmealDtoPage,"records");
        List<SetmealDto> arrayList = new ArrayList();
        List<Setmeal> records = pageInfo.getRecords();//需要拷贝的数据
//        拷贝
        for (Setmeal setmeal : records) {
            //首先我们需要新建一个对象来接受数据
            SetmealDto setmealDto = new SetmealDto();
//            然后可以拷贝过去了
            BeanUtils.copyProperties(setmeal,setmealDto);
//            然后我们开始设置属性
            Category category = categoryService.getById(setmeal.getCategoryId());
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            arrayList.add(setmealDto);


        }
        setmealDtoPage.setRecords(arrayList);

        return R.success(setmealDtoPage);

    }
    /**
     * 保存新增的套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
//        调用dervice的方法来处理主要逻辑就是先把套餐信息保存,然后把餐品名字保存
        setmealSerivce.savebyDto(setmealDto);
        return R.success("新冠套餐成功");
    }
    /**
     * 修改套餐页面
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSet(@PathVariable("id") Long id){
        //首先根据id查询套餐的信息,然后复制到setmelDto上

        //调用service查询到数据
        Setmeal setmeal = setmealSerivce.getById(id);
        SetmealDto setmealDto = new  SetmealDto();
        //复制属性过去
        BeanUtils.copyProperties(setmeal,setmealDto);
        //格局id查询setmeldish的信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<SetmealDish>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //把相关信息设置进去
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);


    }
    /**
     * 修改套餐的提交
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        //首先setmeal做修改的动作
        setmealSerivce.updateById(setmealDto);
        //然后里面的属性做删除然后再插入的动作
        LambdaUpdateWrapper<SetmealDish> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(objectLambdaUpdateWrapper);
        //准备插入数据
        //先获取相应的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes= setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //然后把这些数据存放到数据库之中
        setmealDishService.saveBatch(setmealDishes);
        return R.success("修改成功");


    }
    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(Long id) {
          //根据id更新
        LambdaUpdateWrapper<Setmeal> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(Setmeal::getId, id);
        setmealSerivce.remove(objectLambdaUpdateWrapper);
        //删除stemldish中的套餐的信息
        LambdaUpdateWrapper<SetmealDish> setmealDishLambdaUpdateWrapper = new LambdaUpdateWrapper<SetmealDish>();
        setmealDishLambdaUpdateWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(setmealDishLambdaUpdateWrapper);

        return R.success("删除套餐成功");
    }
    /**
     * 停售菜单
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status")Integer status,Long ids) {
        //主要逻辑就是该局id修改属性状态码
        Setmeal setmeal = setmealSerivce.getById(ids);
        LambdaUpdateWrapper<Setmeal> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.eq(Setmeal::getId, ids);
        //构造更新条件
        if(status ==0){
            objectLambdaUpdateWrapper.set(Setmeal::getStatus,0);
        }else{
            objectLambdaUpdateWrapper.set(Setmeal::getStatus,1);
        }
        setmealSerivce.update(objectLambdaUpdateWrapper);
        return R.success("停售操作成功");
    }
    /**
     * 前端页面的战士套餐的详情
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getList(Setmeal setmeal){
        //构造查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //还有根据状态码查询
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
//        排序
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getCreateTime);
        List<Setmeal> setmealList = setmealSerivce.list(setmealLambdaQueryWrapper);
        return R.success(setmealList);
    }
}
