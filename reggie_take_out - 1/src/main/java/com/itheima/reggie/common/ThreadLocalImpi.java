package com.itheima.reggie.common;

/**
 * 这是用来实现本地线程的公共类
 */
public class ThreadLocalImpi {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void set(Long id){
        threadLocal.set(id);
    }
    public static Long get(){
        return threadLocal.get();
    }
}
