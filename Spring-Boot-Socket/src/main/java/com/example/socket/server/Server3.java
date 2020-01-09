package com.example.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * socket server3
 * @author dengzhiming
 * @date 2019/9/4
 */
@Component
public class Server3 {

    private static Logger logger = LoggerFactory.getLogger(Server3.class);
    //监听的端口号
    private static final int PORT = 8081;

    // @Component+@PostConstruct实现服务端随项目启动而启动
    @PostConstruct
    private void start() {
        SocketThread socketThread = new SocketThread();
        socketThread.start();
    }

    public static class SocketThread extends Thread {
        private ServerSocket serverSocket;

        public SocketThread() {
            try {
                this.serverSocket = new ServerSocket(PORT);
                logger.info("Server Start...");
            } catch (IOException e) {
                logger.error("服务器异常: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
            while (!this.isInterrupted()) {
                //线程未中断则执行循环 
                try {
                    Socket socket = serverSocket.accept();
                    fixedThreadPool.execute(new Runnable() {
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
                    });
                } catch (IOException e) {
                    logger.error("服务器异常: " + e.getMessage());
                }
            }
        }
    }
}
