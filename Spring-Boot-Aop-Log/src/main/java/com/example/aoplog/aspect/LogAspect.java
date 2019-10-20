package com.example.aoplog.aspect;

import com.example.aoplog.annotation.Log;
import com.example.aoplog.bean.SysLog;
import com.example.aoplog.dao.SysLogMapper;
import com.example.aoplog.utils.HttpContextUtils;
import com.example.aoplog.utils.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by dengzhiming on 2019/4/11
 */
@Aspect
@Component
public class LogAspect {
    private final SysLogMapper sysLogMapper;

    @Autowired
    public LogAspect(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    @Pointcut("@annotation(com.example.aoplog.annotation.Log)")
    public void poincut() {
    }

    @Around("poincut()")
    public Object around(ProceedingJoinPoint point) {
        Object result = null;
        long start = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - start;
        saveLog(point, time);
        return result;
    }

    private void saveLog(ProceedingJoinPoint point, long time) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        Log annotation = method.getAnnotation(Log.class);
        if (annotation != null) {
            sysLog.setOperation(annotation.value());
        }
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName);

        Object[] args = point.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                params.append(" ").append(paramNames[i]).append(": ").append(args[i]);
            }
            sysLog.setParams(params.toString());
        }
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        sysLog.setIp(IPUtils.getRealIp(request));
        sysLog.setUserName("admin");
        sysLog.setTime(time);
        sysLog.setCreateTime(new Date());
        sysLogMapper.saveSysLog(sysLog);
    }
}
