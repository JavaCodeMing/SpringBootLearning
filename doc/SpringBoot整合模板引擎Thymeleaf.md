# Spring Boot 整合模板引擎Thymeleaf

```text
Spring Boot支持FreeMarker、Groovy、Thymeleaf和Mustache四种模板解析引擎，官方推荐使用Thymeleaf。
1.引入thymeleaf依赖(推荐使用3.0以上版本,若默认则查看\org\springframework\boot\spring-boot-dependencies\XXX.RELEASE下的pom文件):
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
2.在application.yml或application.properties文件中配置thymeleaf及其他相关配置:
    #设置服务器项目根路径
    server.servlet.context-path=/web
    #一般开发中将spring.thymeleaf.cache设置为false，其他保持默认值即可。
    #开启模板缓存(默认值：true)
    spring.thymeleaf.cache=true 
    #Check that the template exists before rendering it.
    spring.thymeleaf.check-template=true 
    #检查模板位置是否正确（默认值:true）
    spring.thymeleaf.check-template-location=true
    #Content-Type的值（默认值：text/html）
    spring.thymeleaf.content-type=text/html
    #开启MVC Thymeleaf视图解析（默认值：true）
    spring.thymeleaf.enabled=true
    #模板编码
    spring.thymeleaf.encoding=UTF-8
    #要被排除在解析之外的视图名称列表，用逗号分隔
    spring.thymeleaf.excluded-view-names=
    #要运用于模板之上的模板模式。另见StandardTemplate-ModeHandlers(默认值：HTML5)
    spring.thymeleaf.mode=HTML5
    #在构建URL时添加到视图名称前的前缀（默认值：classpath:/templates/）
    spring.thymeleaf.prefix=classpath:/templates/
    #在构建URL时添加到视图名称后的后缀（默认值：.html）
    spring.thymeleaf.suffix=.html
    #Thymeleaf模板解析器在解析器链中的顺序;默认为1,且顺序从1开始,只有在定义了额外的TemplateResolver Bean时才需要设置;
    spring.thymeleaf.template-resolver-order=
    #可解析的视图名称列表，用逗号分隔
    spring.thymeleaf.view-names=
3.编写测试Controller:
    @Controller
    public class IndexController {
	@RequestMapping("/index")
	public String index(Model model){
	    List<User> list = new ArrayList<>();
            list.add(new User("KangKang", "康康", "e10adc3949ba59abbe56e", "超级管理员", "17611111111"));
            list.add(new User("Mike", "麦克", "e10adc3949ba59abbe56e", "管理员", "15111111111"));
            list.add(new User("Jane", "简", "e10adc3949ba59abbe56e", "运维人员", "18611111111"));
            list.add(new User("Maria", "玛利亚", "e10adc3949ba59abbe56e", "清算人员", "15511111111"));
            model.addAttribute("userList", list);
	    return "index";
	}
    }
4.使用thymeleaf模板规范编写展示页面index.html:
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
    	<title>index</title>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    	<link rel="stylesheet" th:href="@{/css/style.css}" type="text/css">
    </head>
    <body>
	<table>
	    <tr>
	    	<th>no</th>
	    	<th>account</th>
	    	<th>name</th>
	    	<th>password</th>
	    	<th>accountType</th>
	    	<th>tel</th>
	    </tr>
	    <tr th:each="list,stat : ${userList}">
	    	<td th:text="${stat.count}"></td>
	    	<td th:text="${list.account}"></td>
	    	<td th:text="${list.name}"></td>
	    	<td th:text="${list.password}"></td>
	    	<td th:text="${list.accountType}"></td>
	    	<td th:text="${list.tel}"></td>
	    </tr>
	</table>
    </body>
5.启动项目,访问测试: http://localhost:8080/web/index
```
