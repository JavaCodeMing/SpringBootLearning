package com.codeming.starter;

/**
 * @author dengzhiming
 * @date 2020/3/5 23:11
 */
public class HelloService {
    HelloProperties helloProperties;

    public String sayHello(String name){
        return "Hello " + name + "，" + helloProperties.getSuffix();
    }

    public HelloProperties getHelloProperties() {
        return helloProperties;
    }

    public void setHelloProperties(HelloProperties helloProperties) {
        this.helloProperties = helloProperties;
    }
}
