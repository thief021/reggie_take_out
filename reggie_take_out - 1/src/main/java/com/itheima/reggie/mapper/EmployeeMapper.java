package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
//之前没有使用mybatis的时候使用的是dao层然后zdmapper映射文件中查询数据,现在使用了一个mapper注解代替了dao层
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
