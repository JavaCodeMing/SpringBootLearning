# 开启Spring Boot

```text
1.创建项目
	[1]通过官方提供的网址创建
		(1)访问http://start.spring.io/
		(2)选择项目类型、编程语言、springboot版本、项目元数据(坐标)、依赖
	[2]通过eclipse或IDEA(以IDEA为例)
		(1)File -> New -> Project -> Spring Initializr -> Next
		(2)填项目元数据(坐标)、项目类型(Maven)、编程语言(Java)、打包方式(war/jar)、Java版本、项目的一些描述(可默认) -> Next
		(3)选择需要添加的依赖(如Web ->勾选web)-> 选择springboot的版本号 -> finish
2.聊聊pom.xml
	[1]<parent>标签: 
	    <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>2.1.3.RELEASE</version>
            <relativePath/>
        </parent>
		(2)定义当前项目的springboot版本信息,spring-boot-starter-parent依赖提供了诸多的默认Maven依赖
		(3)springboot版本声明的具体默认依赖版本可查看maven仓库下\org\springframework\boot\spring-boot-dependencies\XXX.RELEASE下的pom文件
	[2]<dependency>标签:<dependencys>的子标签用于配置所需要的依赖
		其常用的子标签有:<groupId>、<artifactId>、<version>(不填使用springboot默认的)、<scope>(依赖生效范围)、<exclusions>(依赖排除)
	[3]<exclusion>标签:<exclusions>的子标签
		常用标签:<groupId>、<artifactId>
	[4]<build> -> <plugins> -> <plugin>:用于配置Maven插件
		<groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
		Maven插件的作用: 
			把项目打包成一个可执行的超级JAR,包括把应用程序的所有依赖打入JAR文件内,并为JAR添加一个描述文件,其中的内容能让你用java -jar来运行应用程序
			搜索public static void main()方法来标记为可运行类。
3.查看项目配置的依赖包含的隐式依赖:
	在IDEA的终端,切换到项目目录下,执行: mvn dependency:tree
```
