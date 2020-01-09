package com.example.socket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by dengzhiming on 2019/8/22
 */
public class Client {
    // 服务端端口
    private static final int PORT = 8081;
    // 服务端IP
    private static final String HOST = "localhost";
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        logger.info("Client Start...");
        while (true) {
            Socket socket = null;
            try {
                //创建一个套接字对象并将其连接到指定主机上的指定端口号
                socket = new Socket(HOST, PORT);
                //读取服务器端数据
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //向服务器端发送数据
                PrintStream out = new PrintStream(socket.getOutputStream());
                logger.info("请输入: ");
                String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.println(str);
                String ret = input.readLine();
                logger.info("服务器端返回过来的是: " + ret);
                // 如接收到 "OK" 则断开连接
                if ("OK".equals(ret)) {
                    logger.info("客户端正在关闭连接...");
                    Thread.sleep(500);
                    break;
                }
                out.close();
                input.close();
            } catch (Exception e) {
                logger.info("客户端异常:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        logger.info("客户端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }
}
