package com.example.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * socket server: 通过main方法来启动服务端
 * @author dengzhiming
 * @date 2019/8/22
 */
public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    // 监听的端口号
    private static final int PORT = 8081;

    public static void main(String[] args) {
        Server server = new Server();
        server.init();
    }

    private void init() {
        try {
            // new ServerSocket(port[,len]): len为连接请求的队列长度,超过则拒绝
            ServerSocket serverSocket = new ServerSocket(PORT);
            logger.info("Server Start... ");
            while (true) {
                //阻塞等待,若请求队列中有连接,则取出一个
                Socket client = serverSocket.accept();
                // 处理取到的连接
                hand(client);
            }
        } catch (Exception e) {
            logger.error("服务器异常: " + e.getMessage());
        }
    }

    private void hand(Socket socket) {
        try {
            // 读取客户端数据
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
            String clientInputStr = input.readLine();
            // 处理客户端数据
            logger.info("客户端发过来的内容:" + clientInputStr);
            // 向客户端回复信息
            PrintStream out = new PrintStream(socket.getOutputStream());
            logger.info("请输入:");
            // 发送键盘输入的一行
            String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
            out.println(s);
            // 关闭流
            out.close();
            input.close();
        } catch (Exception e) {
            logger.error("服务器 run 异常: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    logger.error("服务端 finally 异常:" + e.getMessage());
                }
            }
        }
    }
}
