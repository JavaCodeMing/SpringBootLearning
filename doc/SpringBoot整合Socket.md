```
1.引入web依赖: (用于提供日志依赖的支持)
	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
2.Socket服务端:
	[1]手动启动版: (需手动启动;占用主线程;)
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
	[2]单线程自动启动版: (随项目启动而启动;不占用主线程,另起一个线程来运行服务)
        @Component
        public class Server1 {
            private static Logger logger = LoggerFactory.getLogger(Server1.class);
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
                        logger.info("Server1 Start...");
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
                            hand(client);
                        } catch (IOException e) {
                            logger.error("服务器异常: " + e.getMessage());
                        }
                    }
                }
                private void hand(Socket socket) {
                    try {
                        // 读取客户端数据
                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
	[3]自动启动多线程处理版: (随项目启动而启动;不占用主线程,另起单个线程来运行服务,多线程处理)
        @Component
        public class Server2 {
            private static Logger logger = LoggerFactory.getLogger(Server2.class);
            private static final int PORT = 8081;//监听的端口号
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
	[4]自动启动多线程处理优化版: (解决上一版本无限创建线程的问题)
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
3.通用客户端:
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
```
