```
1.在IDEA中使用自带的打包方式打包或自带的maven管理工具打包:
    [1]自带的打包方式: 
        file -> Project Structure -> 点击"+" -> JAR -> from modules with dependencies... ->
        Main Class: 选择项目的入口类 -> JAR files from libraries : 选第二个 -> Directory for META-INF/MANIFEST.MF:
        选择目录:xxx\src\main\resources -> 点击OK -> 点击<output root>,右键点击"Create Directory"创建libs目录 
        -> 将项目依赖jar包都移入libs目录 -> 点击项目的jar,修改"Class Path"内容,给所有依赖包添加"libs/"路径 ->
        -> 点击Apply,点击OK -> 点击Build菜单,点击Build Artifact...,点击Build或Rebuild
        (jar包默认生成在"out\artifacts\xxx\"目录)
    [2]自带的maven管理工具: 
        点击View菜单 -> 勾选"Tool Buttons" -> 点击右侧的Maven按钮 -> 依次执行Lifecycle里的clean,compile,package
        (jar包生成在target目录下)
2.将生成的目录和jar包传到linux上:
    [1]自带的打包方式: 
        打包时生成了项目的jar包和libs文件夹(存放着项目依赖的jar包),把这项目jar包和libs文件夹传到linux的一个目录下
    [2]自带的maven管理工具:
        打包时只生成了一个项目jar包,只需将项目jar包传到linux的一个目录下即可
3.在linux上安装JDK:
    [1]使用root用户在usr目录下建立java安装目录: mkdir /usr/java/
    [2]将jdk的压缩包放到目录"/usr/java/"下,并解压到当前目录: tar -zxvf jdk-8u60-linux-x64.tar.gz(得到文件夹 jdk1.8.0_60)
    [3]安装完毕为他建立一个链接以节省目录长度: ln -s /usr/java/jdk1.8.0_60/ /usr/jdk
    [4]编辑配置文件,配置环境变量: vim /etc/profile 添加如下内容:
        JAVA_HOME=/usr/jdk
        CLASSPATH=$JAVA_HOME/lib/
        PATH=$PATH:$JAVA_HOME/bin
        export PATH JAVA_HOME CLASSPATH
    [5]重启机器或执行source命令: sudo shutdown -r now | source /etc/profile
    [6]查看安装情况: java -version
4.在linux上部署应用: 
    (nohup命令可以将程序以忽略挂起信号的方式运行起来,被运行的程序的输出信息将不会显示到终端)
    [1]首次部署当前程序需要在对应的文件夹中执行以下命令
        a.启动程序 nohup java -jar XXX.jar &
        b.退出 ctrl + c
        c.查看日志 tail -500f nohup.out
    [2]非首次部署当前程序需要在对应的文件夹中执行以下命令
        a.捕获上一个版本程序的进程 ps - ef|grep XXX.jar
        b.杀死对应的进程 kill 进程号
        c.启动程序 nohup java -jar demo01.jar &
        d.退出 ctrl + c
        e.查看日志 tail -500f nohup.out
5.若是要实现远程访问还需要配置防火墙的开放端口:
    [1]Centos 7:
        (1)查看已经开放的端口: firewall-cmd --list-ports
        (2)开放端口: firewall-cmd --zone=public --add-port=80/tcp --permanent
            –zone #作用域
            –add-port=80/tcp #添加端口，格式为：端口/通讯协议
            –permanent #永久生效，没有此参数重启后失效
        (3)关停防火墙: 
            firewall-cmd --reload                 #重启firewall
            systemctl stop firewalld.service      #停止firewall
            systemctl disable firewalld.service   #禁止firewall开机启动
            firewall-cmd --state                  #查看默认防火墙状态(关闭后显示notrunning,开启后显示running)
    [2]CentOS 7 以下版本:
        (1)查看开放的端口: /etc/init.d/iptables status
        (2)开放端口: /sbin/iptables -I INPUT -p tcp --dport 端口号 -j ACCEPT 
            保存设置: /etc/rc.d/init.d/iptables save
        (3)关闭防火墙:
            1)永久性生效,重启后不会复原:
                开启：chkconfig iptables on
                关闭：chkconfig iptables off
            2)即时生效,重启后复原
                开启: service iptables start
                关闭: service iptables stop
        (4)查看防火墙状态: service iptables status
```
