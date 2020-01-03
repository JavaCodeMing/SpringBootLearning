```
热部署: 在你修改了后端代码后不需要手动重启,工具会帮你快速的自动重启使修改生效;
原理:使用两个ClassLoader,一个Classloader加载那些不会改变的类(第三方Jar包),另一个ClassLoader
    加载会更改的类,称为restart ClassLoader,这样在有代码更改的时候,原来的restart ClassLoader
    被丢弃,重新创建一个restart ClassLoader,由于需要加载的类相比较少,所以实现了较快的重启时间;
[1]引入Spring-Boot-devtools依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
[2]IDEA启动自动编译功能:
    File -> Settings -> Build,Execution,Deployment -> Compile -> 勾选"Build project automatically"
[3]IDEA设置为在程序运行过程中,依然允许自动编译:
    操作: ctrl + shift + alt + / ,选择Registry,勾选勾上"Compiler autoMake allow when app running"
```
