package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.CategoryService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.ThreadLocalImpi;
import com.itheima.reggie.entity.Category;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    //发送post请求
    @PostMapping
    public R<String> category(HttpServletRequest request, @RequestBody Category category) {
        ThreadLocalImpi.set((Long) request.getSession().getAttribute("employee"));
        categoryService.save(category);
        return R.success("保存成功");
    }
    /**
     * 执行分页查询
     */
    @GetMapping("/page")
    public R<Page> pageQuey(Long page,Long pageSize){
        Page page1 = new Page(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.orderByDesc(Category::getCreateTime);
        lambdaQueryWrapper.eq(Category::getIsDeleted,0);
        //传过来的参数是什么?
        categoryService.page(page1, lambdaQueryWrapper);
        return R.success(page1);

    }
    /**
     * 实施删除的操作更新isdeleted为1
     */
    @DeleteMapping()
    public R<String> delete(Long ids){
        //该局id查询数据的信息
        log.info("id={}",ids);
        categoryService.update(ids);

        //调用更新方法
        return R.success("删除成功");

    }
    /**
     * 修改菜品分类
     */
    @PutMapping
    public R<String> put(HttpServletRequest request, @RequestBody Category category){

      ThreadLocalImpi.set((Long)request.getSession().getAttribute("employee"));
        categoryService.updateById(category);
      return R.success("操作成功");
    }
/**
 * 修改分类的信息
 */
  @GetMapping("/list")
  public R<List<Category>> list(Category category){
      //他传过来的是一个id
      LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
      //输入查询条件
      lambdaQueryWrapper.eq(category.getType()!=null, Category::getType,category.getType());
      List<Category> list = categoryService.list(lambdaQueryWrapper);
      return R.success(list);

    }
}
