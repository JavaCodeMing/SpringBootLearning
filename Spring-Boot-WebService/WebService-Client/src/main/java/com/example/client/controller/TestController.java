package com.example.client.controller;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Created by dengzhiming on 2019/8/4
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public void test() throws Exception {
        //创建动态客户端
        JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
        Client client = factory.createClient("http://localhost:8080/demo/api?wsdl");
        // 需要密码的情况需要加上用户名和密码
        // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,PASS_WORD));
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(2000);  //连接超时
        httpClientPolicy.setAllowChunking(false);     //取消块编码
        httpClientPolicy.setReceiveTimeout(120000);   //响应超时
        conduit.setClient(httpClientPolicy);
        //client.getOutInterceptors().addAll(interceptors);//设置拦截器
        try {
            Object[] objects;
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke("sayHello", "qiqi");
            System.out.println("返回数据:" + objects[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/testWeather")
    public void testWeather() {
        String weatherInfo = getWeather("北京");
        System.out.println(weatherInfo);
    }

    /**
     * 对服务器端返回的XML进行解析
     *
     * @param city 用户输入的城市名称
     * @return 字符串 用#分割
     */
    private static String getWeather(String city) {
        try {
            Document doc;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = getSoapInputStream(city);
            assert is != null;
            doc = db.parse(is);
            NodeList nl = doc.getElementsByTagName("string");
            StringBuffer sb = new StringBuffer();
            for (int count = 0; count < nl.getLength(); count++) {
                Node n = nl.item(count);
                if (n.getFirstChild().getNodeValue().equals("查询结果为空！")) {
                    sb = new StringBuffer(" ");
                    break;
                }
                sb.append(n.getFirstChild().getNodeValue()).append(" \n");
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从接口文档中获取SOAP的请求头,并替换其中的标志符号为用户输入的城市
     * (方法的接口文档: http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx?op=getWeatherbyCityName)
     * @param city 用户输入的城市名称
     * @return 客户将要发送给服务器的SOAP请求
     */
    private static String getSoapRequest(String city) {
        String sb = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body> <getWeatherbyCityName xmlns=\"http://WebXml.com.cn/\">" +
                "<theCityName>" +
                city +
                "</theCityName> </getWeatherbyCityName>" +
                "</soap:Body></soap:Envelope>";
        return sb;
    }

    /**
     * 通过接口文档的请求头来构建SOAP请求,发送SOAP请求给服务器端,并返回流
     *
     * @param city 用户输入的城市名称
     * @return 服务器端返回的输入流，供客户端读取
     * @throws Exception 异常
     */
    private static InputStream getSoapInputStream(String city) throws Exception {
        try {
            String soap = getSoapRequest(city);
            // 通过请求的服务地址(即为Endpoint)构建URL对象,并使用URL对象开启连接
            URL url = new URL("http://ws.webxml.com.cn/WebServices/WeatherWebService.asmx");
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // 为连接设置请求头属性
            conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction", "http://WebXml.com.cn/getWeatherbyCityName");
            // 将请求的XML信息写入连接的输出流
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(soap);
            osw.flush();
            osw.close();
            // 获取连接中请求得到的输入流
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
