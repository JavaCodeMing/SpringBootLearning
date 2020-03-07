# FastDFS架构图:   
![image](https://note.youdao.com/yws/public/resource/959ce5f7753c42f74674f9c144159f74/xmlnote/9427D8BC29AC412EB39223C79A8E15C3/13252)
# FastDFS流程图:   
![image](https://note.youdao.com/yws/public/resource/959ce5f7753c42f74674f9c144159f74/xmlnote/78E200833E6A461DA1FA9E31DBF2D342/13258)

```test
1.FastDFS基本介绍:
    [1]跟踪服务器(Tracker Server): 跟踪服务器,主要做调度工作,起到均衡的作用;负责管理所有的storage server;
    [2]存储服务器(Storage Server): 存储服务器,主要提供容量和备份服务;以group为单位,每个group 内可以有多台
        storage server,数据互为备份;
    [3]客户端(Client): 客户端,上传下载数据的服务器;即项目所部署在的服务器;
2.上传流程说明:
    [1]客户端发送上传请求到Tracker Server服务器,接着Tracker Server服务器按一定规则分配group和Storage Server;
    [2]Storage Server服务器生成一个file_id: 包括storage server ip,文件创建时间,文件大小,文件 CRC32 校验码和随机数;
    [3]Storage Server会按文件file_id进行两次 hash ，路由到存储目录的其中一个子目录,然后将文件存储到该子目录下,
        最后生成文件路径: group名称,虚拟磁盘路径,数据两级目录,file_id和文件后缀就是一个完整的文件地址;
3.FastDFS在CentOS7下配置安装部署:
    [1]基础准备:
        yum -y install gcc-c++
        yum -y install pcre pcre-devel
        yum -y install zlib zlib-devel
        yum -y install openssl openssl-devel
    [2]安装libfastcommon:
        (1)下载安装 libfastcommon: (查看版本: https://github.com/happyfish100/libfastcommon/releases)
            wget https://github.com/happyfish100/libfastcommon/archive/V1.0.43.tar.gz
        (2)解压libfastcommon: tar -zxvf V1.0.43.tar.gz
        (3)移动解压目录到/usr/local下: mv libfastcommon-1.0.43 /usr/local/
        (4)进入libfastcommon-1.0.43目录,执行编译命令:
            cd /usr/local/libfastcommon-1.0.43
            ./make.sh
        (5)执行安装命令: ./make.sh install
    [3]安装FastDFS:
        (1)下载安装FastDFS: (查看版本: https://github.com/happyfish100/fastdfs/releases)
            wget https://github.com/happyfish100/fastdfs/archive/V6.06.tar.gz
        (2)解压FastDFS: tar -zxvf V6.06.tar.gz
        (3)移动解压目录到/usr/local下: mv fastdfs-6.06/ /usr/local/
        (4)进入fastfds-6.06目录,执行编译命令:
            cd /usr/local/fastdfs-6.06
            ./make.sh
        (5)执行安装命令: ./make.sh install
    [3]配置Tracker服务:
        (1)安装成功后,进入/etc/fdfs目录,将tracker.conf.sample文件改为tracker.conf配置文件并修改它:
            cd /etc/fdfs/
            cp /etc/fdfs/tracker.conf.sample /etc/fdfs/tracker.conf
            vim /etc/fdfs/tracker.conf    //修改需要修改的参数
                1)disabled=false            //启用配置文件
                1)port=22122                //设置tracker的端口号,通常采用22122这个默认端口
                1)base_path=/opt/fastdfs    //设置tracker的数据文件和日志目录
                2)http.server_port=6666     //tracker服务器http端口
        (2)修改完成后我们需要建立tracker的工作目录: (配置中base_path对应的值)
            mkdir -p /opt/fastdfs
        (3)启动tracker服务: (启动为start,停止为stop)
            /usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start
        (4)查看监听: (若能查看到fdfs的进程则Tracker服务安装成功)
            ps -ef|grep fdfs 或 netstat -lnpt|grep fdfs
    [4]配置Storage服务: (Storage 服务也可放在多台服务器,它有 Group(组)的概念,同一组内服务器互备同步;此处只配置单机;)
        (1)进入/etc/fdfs/目录,将storage.conf.sample文件改为storage.conf配置文件并修改它:
            cd /etc/fdfs/
            cp /etc/fdfs/storage.conf.sample /etc/fdfs/storage.conf
            vim /etc/fdfs/storage.conf
        (2)打开storage.conf文件后,找到下面参数进行修改:
            1)disabled=false
            2)group_name=group1
            3)port=23000
            4)base_path=/opt/fastdfs
            5)store_path_count=1
            6)store_path0=/opt/fastdfs/fdfs_storage
            7)tracker_server=192.168.8.120:22122
            8)http.server_port=8888
        (3)修改完成后,需要建立storage的数据存储目录:
            mkdir -p /opt/fastdfs/fdfs_storage
        (4)启动storage服务: (启动为start,停止为stop)
            /usr/bin/fdfs_storaged /etc/fdfs/storage.conf start
        (5)查看监听:
            ps -ef|grep fdfs 或 netstat -lnpt|grep fdfs
            (若此时tracker和storage都已启动,则/opt/fastdfs文件夹下会出现一大堆文件夹用于存放文件用)
        (6)安装配置并启动了Tracker和Storage服务,监视他们之间的通信:
            /usr/bin/fdfs_monitor /etc/fdfs/storage.conf
            (看到Storage下的ip_addr后显示ACTIVE,则说明ok)
    [5]配置fdfs_upload_file上传文件:
        (1)进入/etc/fdfs目录,将client.conf.sample文件改为client.conf配置文件并修改它:
             /etc/fdfs/client.conf,若弄错目录,上传时会报错)
            cd /etc/fdfs/
            cp /etc/fdfs/client.conf.sample /etc/fdfs/client.conf
            vim client.conf 
        (2)找到下面参数进行修改:
            1)base_path=/opt/fastdfs
            2)tracker_server=192.168.8.120:22122
            3)http.tracker_server_port=6666
            (cp /etc/fdfs/client.conf /usr/etc/fdfs/client.conf)
        (3)修改完成后就可通过/usr/bin/fdfs_upload_file命令上传文件,/usr/bin/fdfs_upload_file 命令用法:
            /usr/bin/fdfs_upload_file <config_file> <local_filename> [storage_ip:port] [store_path_index]
            (命令 配置文件 上传的文件 storage_ip和端口 store_path角标)
            (文件上传后会返回文件所在路径,只要把这路径保存起来,后续集成Nginx就可访问到上传的文件了)
            /usr/bin/fdfs_upload_file /etc/fdfs/client.conf /home/sherry/Downloads/wechat.jpg
    [6]tracker.conf和storage.conf的具体配置注释:
        (1)tracker.conf:
            //设置该配置文件是否是否不启用,因为在启动fastdfs服务端进程时需要指定配置文件,
            //所以需要使次配置文件生效;false是生效,true是屏蔽;
            disabled=false
            //程序的监听地址,如果不设定则监听所有地址
            bind_addr=
            //tracker监听的端口
            port=22122
            //连接超时设定
            connect_timeout=30
            //tracker在通过网络发送接收数据的超时时间
            network_timeout=60
            //数据和日志的存放地点
            base_path=/opt/fdfs
            //服务所支持的最大链接数
            max_connections=256
            //工作线程数一般为cpu个数
            work_threads=4
            //在存储文件时选择group的策略:
            //  0:轮训策略 
            //  1:指定某一个组
            //  2:负载均衡,选择空闲空间最大的group
            store_lookup=2
            //如果上面的store_lookup选择了1,则这里需要指定一个group
            //store_group=group2
            // group中的哪台storage做主storage,当一个文件上传到主storage后,
            //就由这台机器同步文件到group内的其他storage上,
            //  0:轮训策略 
            //  1:根据ip地址排序,第一个 
            //  2:根据优先级排序,第一个
            store_server=0
            //选择那个storage作为主下载服务器:
            //  0:轮训策略
            //  1:主上传storage作为主下载服务器
            download_server=0
            //选择文件上传到storage中的哪个(目录/挂载点),storage可以有多个存放文件的base path 
            //  0:轮训策略
            //  2:负载均衡,选择空闲空间最大的
            store_path=0
            //系统预留空间,当一个group中的任何storage的剩余空间小于定义的值,整个group就不能上传文件了
            reserved_storage_space = 4GB
            //日志信息级别
            log_level=info
            //进程以那个用户/用户组运行,不指定默认是当前用户
            run_by_group=
            run_by_user=
            //允许那些机器连接tracker默认是所有机器
            allow_hosts=*
            //设置日志信息刷新到disk的频率,默认10s
            sync_log_buff_interval = 10
            //检测storage服务器的间隔时间,storage定期主动向tracker发送心跳,
            //如果在指定的时间没收到信号,tracker人为storage故障,默认120s
            check_active_interval = 120
            //线程栈的大小,最小64K
            thread_stack_size = 64KB
            //storage的ip改变后服务端是否自动调整,storage进程重启时才自动调整
            storage_ip_changed_auto_adjust = true
            //storage之间同步文件的最大延迟,默认1天
            storage_sync_file_max_delay = 86400
            //同步一个文件所花费的最大时间
            storage_sync_file_max_time = 300
            //是否用一个trunk文件存储多个小文件
            use_trunk_file = false
            //最小的solt大小,应该小于4KB,默认256bytes
            slot_min_size = 256
            //最大的solt大小,如果上传的文件小于默认值,则上传文件被放入trunk文件中
            slot_max_size = 16MB
            //trunk文件的默认大小,应该大于4M
            trunk_file_size = 64MB
            //http服务是否生效,默认不生效
            http.disabled=false
            //http服务端口
            http.server_port=8080
            //检测storage上http服务的时间间隔，<=0表示不检测
            http.check_alive_interval=30
            //检测storage上http服务时所用请求的类型,tcp只检测是否可以连接,http必须返回200
            http.check_alive_type=tcp
            //通过url检测storage http服务状态
            http.check_alive_uri=/status.html
            //if need find content type from file extension name
            http.need_find_content_type=true
            // 用include包含进http的其他设置
            // include http.conf
        (1)storage.conf:
            //同tracker.conf
            disabled=false
            //这个storage服务器属于那个group
            group_name=group1
            //同tracker.conf
            bind_addr=
            //连接其他服务器时是否绑定地址,bind_addr配置时本参数才有效
            client_bind=true
            //同tracker.conf
            port=23000
            connect_timeout=30
            network_timeout=60
            //主动向tracker发送心跳检测的时间间隔
            heart_beat_interval=30
            //主动向tracker发送磁盘使用率的时间间隔
            stat_report_interval=60
            //同tracker.conf
            base_path=/opt/fdfs
            max_connections=256
            //接收/发送数据的buff大小,必须大于8KB
            buff_size = 256KB
            //同tracker.conf
            work_threads=4
            //磁盘IO是否读写分离
            disk_rw_separated = true
            //是否直接读写文件,默认关闭
            disk_rw_direct = false
            //混合读写时的读写线程数
            disk_reader_threads = 1
            disk_writer_threads = 1
            //同步文件时如果binlog没有要同步的文件,则延迟多少毫秒后重新读取,0表示不延迟
            sync_wait_msec=50
            //同步完一个文件后间隔多少毫秒同步下一个文件,0表示不休息直接同步
            sync_interval=0
            //表示这段时间内同步文件
            sync_start_time=00:00
            sync_end_time=23:59
            //同步完多少文件后写mark标记
            write_mark_file_freq=500
            //storage在存储文件时支持多路径,默认只设置一个
            store_path_count=1
            //配置多个store_path路径,从0开始,如果store_path0不存在,则base_path必须存在
            store_path0=/opt/fdfs
            //store_path1=/opt/fastdfs2
            //subdir_count  *             subdir_count个目录会在store_path下创建,采用两级存储
            subdir_count_per_path=256
            //设置tracker_server
            tracker_server=x.x.x.x:22122
            //同tracker.conf
            log_level=info
            run_by_group=
            run_by_user=
            allow_hosts=*
            //文件在数据目录下的存放策略,0:轮训 1:随机
            file_distribute_path_mode=0
            //当问及是轮训存放时,一个目录下可存放的文件数目
            file_distribute_rotate_count=100
            //写入多少字节后就开始同步,0表示不同步
            fsync_after_written_bytes=0
            //刷新日志信息到disk的间隔
            sync_log_buff_interval=10
            //同步storage的状态信息到disk的间隔
            sync_stat_file_interval=300
            //线程栈大小
            thread_stack_size=512KB
            //设置文件上传服务器的优先级,值越小越高
            upload_priority=10
            //是否检测文件重复存在,1:检测 0:不检测
            check_file_duplicate=0
            //当check_file_duplicate设置为1时,次值必须设置
            key_namespace=FastDFS
            //与FastDHT建立连接的方式 0:短连接 1:长连接
            keep_alive=0
            //同tracker.conf
            http.disabled=false
            http.domain_name=
            http.server_port=8888
            http.trunk_size=256KB
            http.need_find_content_type=true
            //include http.conf
4.FastDFS配置Nginx模块及访问测试:
    [1]配置fastdfs-nginx-module插件:
        (1)下载fastdfs-nginx-module: (查看版本:https://github.com/happyfish100/fastdfs-nginx-module/releases)
            wget https://github.com/happyfish100/fastdfs-nginx-module/archive/V1.22.tar.gz
        (2)解压fastdfs-nginx-module: 
            tar -zxvf V1.22.tar.gz
        (3)将解压后的文件夹移动到nginx源码文件夹下: 
            mv fastdfs-nginx-module-1.22 /usr/local/
        (4)拷贝mod-fastdfs.conf到/etc/fdfs/文件目录下,配置mod-fastdfs.conf:
            cp /usr/local/fastdfs-nginx-module-1.22/src/mod_fastdfs.conf /etc/fdfs/
            vim /etc/fdfs/mod_fastdfs.conf
                1)base_path=/opt/fastdfs
                2)tracker_server=192.168.8.120:22122
                3)url_have_group_name = true
                4)store_path0=/opt/fastdfs/fdfs_storage
    [2]安装Nginx:
        (1)下载nginx: (查看版本:https://github.com/nginx/nginx/releases)
            wget http://nginx.org/download/nginx-1.17.8.tar.gz
        (2)解压nginx:
            tar -zxvf nginx-1.17.8.tar.gz
        (3)移动解压目录到/usr/local下:
            mv nginx-1.17.8 /usr/local/
        (4)进入nginx-1.17.8目录,执行编译安装命令:
            cd /usr/local/nginx-1.17.8
            //添加fastdfs-nginx处理模块(该模块也可在Nginx安装后添加)
            ./configure --add-module=/usr/local/fastdfs-nginx-module-1.22/src/
            make
            make install
        (5)查看已经安装的nginx模块: (configure arguments: 后面表示当前已经安装的nginx模块)
            /usr/local/nginx/sbin/nginx -V
        (5)启动nginx: (./configure不指定目录安装的话是安装在/usr/local/nginx)
            /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
        (6)启动nginx成功后验证: 浏览器访问localhost:80
        (7)把fastdfs-6.06下面的配置中还没有存在/etc/fdfs中的拷贝进去:
            cd /usr/local/fastdfs-6.06/conf/
            cp anti-steal.jpg http.conf mime.types /etc/fdfs/
            cd /etc/fdfs/
        (8)配置Nginx,编辑nginx.conf文件,添加以下内容: 
            vim /usr/local/nginx/conf/nginx.conf
                server {
                    listen       80;
                    server_name  192.168.8.120;
                    location ~/(group[0-9])/M00 {
                        ngx_fastdfs_module;
                    }
                }
            
        (9)启动Nginx,会打印出fastdfs模块的pid,看看日志是否报错,正常不会报错的;
            /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
        (10)上传文件:
            /usr/bin/fdfs_upload_file <config_file> <local_filename> [storage_ip:port] [store_path_index]
            例: /usr/bin/fdfs_upload_file /usr/etc/fdfs/client.conf /home/conan/Downloads/wechat.jpg
        (11)进行访问测试: 192.168.8.120:80/上传时返回的地址
```
