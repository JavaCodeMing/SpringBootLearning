package com.example.filterlistenerinterceptor.listener;

import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by dengzhiming on 2019/6/1
 */
@Component
@WebListener
public class CustomListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        System.out.println("Listener 销毁");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("Listener 初始化");
    }
}
