package com.itheima.reggie.common;

import ch.qos.logback.classic.Logger;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.itheima.reggie.common.ThreadLocalImpi;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MeatObject implements MetaObjectHandler {
    /**
     * *执行插入的时候的逻辑
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
//        填充字段
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long id =Thread.currentThread().getId();
        log.info("线程id{}",id);
        Long id1 = ThreadLocalImpi.get();
        metaObject.setValue("createUser", id1);
        metaObject.setValue("updateUser", id1);

    }

    /**
     * 执行更新时候的逻辑
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateUser", ThreadLocalImpi.get());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
