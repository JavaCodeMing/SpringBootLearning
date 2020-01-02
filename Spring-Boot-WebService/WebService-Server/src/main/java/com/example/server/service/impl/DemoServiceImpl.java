package com.example.server.service.impl;

import com.example.server.service.DemoService;

import javax.jws.WebService;
import java.util.Date;

/**
 * Created by dengzhiming on 2019/8/4
 */
// serviceName: 对外发布的服务名;
// targetNamespace: 指定名称空间,一般使用接口实现类的包名的反缀;
// endpointInterface: 服务接口的全类名;
@WebService(serviceName = "DemoService"
        , targetNamespace = "http://impl.service.server.example.com"
        , endpointInterface = "com.example.server.service.DemoService")
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String user) {
        return user + ",现在的时间: " + new Date();
    }
}
