package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Service.EmployeeService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.ThreadLocalImpi;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R login(HttpServletRequest request, @RequestBody Employee employee) {
        //实现登录的工程
        //第一步先得到用户名
        String username = employee.getUsername();
        //根据用户名查询数据库的信息
        //创建一个查询结果级
        LambdaQueryWrapper<Employee> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        创建一个该局username查询的结果集
        objectLambdaQueryWrapper.eq(Employee::getUsername, username);
        //根据service进行查询
        Employee employee1 = employeeService.getOne(objectLambdaQueryWrapper);
        //进行判断
        if (employee1 == null) {
            return R.error("登录失败");
        }
        //验证密码
        //先得到密码
        String password = employee.getPassword();
        //加密密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!employee1.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (employee1.getStatus() == 0) {
            return R.error("账号锁定");
        }
        //把数据用户存到浏览器中
        request.getSession().setAttribute("employee", employee1.getId());
        return R.success(employee1);


    }

    /**
     * 退出页面
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清楚session即可
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增用户
     */
    @PostMapping()
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request) {
        //首先第一件事是设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        设置时间,在公共填充字段里面已经填充了,所以无需填充
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        设置操作id通过线程来获取
//        Long employee1 = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employee1);
////        新增创建者
//        employee.setCreateUser(employee1);
        //调用服务方的方法
        Long id =Thread.currentThread().getId();
        log.info("save线程id{}",id);
        ThreadLocalImpi.set((Long) request.getSession().getAttribute("employee"));
        employeeService.save(employee);
        return R.success("保存成功");
        //添加信息

    }

    /**
     * 实现用户查询
     */
    @GetMapping("/page")
    public R<Page> pageinfo(int page, int pageSize, String name) {
        //开启分页
        Page pageinfo = new Page(page, pageSize);
        //构造查询条件,
        LambdaQueryWrapper<Employee> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.like(!StringUtils.isEmpty(name), Employee::getUsername, name);
        objectLambdaQueryWrapper.orderByDesc(Employee::getCreateTime);
        //查询数据
        employeeService.page(pageinfo, objectLambdaQueryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 启用和禁用员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        首先要设置传过来emplyee中的参数
        employee.setUpdateTime(LocalDateTime.now());
//        设置设置者
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        //调用service方法
        employeeService.updateById(employee);
        return R.success("操作成功");

    }

    /**
     * 编辑的时候根据ID查找信息,返回到前端页面
     */
    @GetMapping("/{id}")
    public R<Employee> get(@PathVariable("id") Long id) {
        //调用service方法查询信息
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
    /**
     * 根据穿过来的employee对象修改对应的信息
     */

}