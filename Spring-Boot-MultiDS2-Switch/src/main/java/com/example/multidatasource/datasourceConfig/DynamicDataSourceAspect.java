package com.example.multidatasource.datasourceConfig;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源Advice
 * Created by dengzhiming on 2019/3/29
 */
@Aspect
//1.保证该AOP在@Transactional之前执行;
//2.保证拦截器ExposeInvocationInterceptor在拦截链开头,其优先级为HIGHEST_PRECEDENCE+1
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Component
public class DynamicDataSourceAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /*
     * @Before("@annotation(ds)")的意思是：
     *  @Before：在方法执行之前进行执行：
     *  @annotation(targetDataSource)：会拦截注解targetDataSource的方法，否则不拦截;
     */
    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {
        //获取当前的指定的数据源;
        String dsId = targetDataSource.value();
        //如果不在配置的数据源范围之内,则输出警告信息,且系统自动使用默认的数据源
        if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
            log.info("数据源[{}]不存在，使用默认数据源 > {}", targetDataSource.value(), point.getSignature());
        } else {
            log.info("Use DataSource : {} > {}", targetDataSource.value(), point.getSignature());
            //找到的话,那么设置到动态数据源上下文中
            DynamicDataSourceContextHolder.setDataSourceType(dsId);
        }
    }

    @After("@annotation(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        log.info("Revert DataSource : {} > {}", targetDataSource.value(), point.getSignature());
        //方法执行完毕之后,销毁当前数据源信息,进行垃圾回收
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
