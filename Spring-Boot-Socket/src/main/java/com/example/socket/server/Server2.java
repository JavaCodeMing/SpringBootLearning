package com.example.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * socket server2
 * @author dengzhiming
 * @date 2019/9/4
 */
//@Component
public class Server2 {

    private static Logger logger = LoggerFactory.getLogger(Server2.class);
    //监听的端口号
    private static final int PORT = 8081;

    // @Component+@PostConstruct实现服务端随项目启动而启动
    //@PostConstruct
    private void start() {
        SocketThread socketThread = new SocketThread();
        socketThread.start();
    }

    public static class SocketThread extends Thread {
        private ServerSocket serverSocket;

        public SocketThread() {
            try {
                this.serverSocket = new ServerSocket(PORT);
                logger.info("Server2 Start...");
            } catch (IOException e) {
                logger.error("服务器异常: " + e.getMessage());
            }

        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                //线程未中断则执行循环 
                try {
                    Socket client = serverSocket.accept();
                    new HandlerThread(client);
                } catch (IOException e) {
                    logger.error("服务器异常: " + e.getMessage());
                }
            }
        }
    }

    public static class HandlerThread implements Runnable {
        private Socket socket;

        HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                // 读取客户端数据
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                String clientInputStr = input.readLine();
                // 处理客户端数据
                logger.info("客户端发过来的内容:" + clientInputStr);
                // 向客户端回复信息
                PrintStream out = new PrintStream(socket.getOutputStream());
                logger.info("请输入:");
                // 发送键盘输入的一行
                String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.println(s);
                //关闭流
                out.close();
                input.close();
            } catch (Exception e) {
                logger.info("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        logger.info("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }
}
