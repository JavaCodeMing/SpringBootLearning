package com.example.server.service;

import javax.jws.WebService;

/**
 * Created by dengzhiming on 2019/8/4
 */
// name: Web Service的名称;targetNamespace: 指定名称空间,一般使用接口实现类的包名的反缀
@WebService(name = "DemoService", targetNamespace = "http://impl.service.server.example.com")
public interface DemoService {
    String sayHello(String user);
}
