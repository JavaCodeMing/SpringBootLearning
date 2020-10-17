```
1.引入Hibernate Validator依赖和工具类依赖:
    <!--spring-boot-starter-web已经包含了hibernate-validator,无需单独引入-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependencies>
2.hibernate-validator的校验注解:
    (位于javax.validation.constraints和org.hibernate.validator.constraints包中)
    注解                        描述
    @Null                       限制只能为null
    @NotNull                    限制必须不为null
    @AssertFalse                限制必须为false
    @AssertTrue                 限制必须为true
    @DecimalMax(value)          限制必须为一个不大于指定值的数字
    @DecimalMin(value)          限制必须为一个不小于指定值的数字
    @Digits(integer,fraction)   限制必须为一个小数,且整数部分的位数不能超过
                                   integer,小数部分的位数不能超过fraction
    @Future                     限制必须是一个将来的日期
    @Past                       限制必须是一个过去的日期
    @Max(value)                 限制必须为一个不大于指定值的数字
    @Min(value)                 限制必须为一个不小于指定值的数字
    @Past                       限制必须是一个过去的日期
    @Pattern(value)             限制必须符合指定的正则表达式
    @Size(max,min)              限制字符长度必须在min到max之间
    @SafeHtml                   字符串是安全的html
    @URL                        字符串是合法的URL
    @NotBlank                   字符串必须有字符
    @NotEmpty                   字符串不为NULL,集合有字符
    @AssertFalse                必须是false
    @AssertTrue                 必须是true
3.方法参数校验: (使用hibernate-validator注解修饰方法的参数)
    [1]使用校验注解修饰方法的参数,并在当前类上添加@Validated注解:
        @Controller
        @Validated
        public class TestParamController {
            @GetMapping("/test1")
            @ResponseBody
            public String test1(
                    @NotNull(message = "{required}") String name,
                    @Email(message = "{invalid}") String email){
                return "success";
            }
        }
        (要让参数校验生效,还需在使用校验注解的类上使用@Validated注解标注)
        (校验注解的message属性用于校验不通过时的信息提示,其属性值为占位符,需要额外配置)
    [2]为校验注解的的message属性配置占位符的内容:
        (占位符的内容默认要在src/main/resources路径下的ValidationMessages.properties中定义)
        ValidationMessages.properties:
            required=\u4e0d\u80fd\u4e3a\u7a7a
            invalid=\u683c\u5f0f\u4e0d\u5408\u6cd5
            (内容为中文转Unicode后的值,http://tool.chinaz.com/tools/unicode.aspx可进行转换)
4.实体传参属性校验: (使用hibernate-validator注解JavaBean的属性)
    [1]给需要校验的实体类属性添加校验注解:
        public class User implements Serializable {
            private static final long serialVersionUID = -2731598327208972274L;
            @NotBlank(message = "{required}")
            private String name;
            @Email(message = "{invalid}")
            private String email;
            
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
        }
    [2]使用@Valid修饰参数为包含校验属性对象:
        @Controller
        public class TestBeanController {
            @GetMapping("test2")
            @ResponseBody
            public String test2(@Valid User user){
                return "success";
            }
        }
        (使用实体对象传参的方式参数校验需要在相应的参数前加上@Valid注解)
5.分组校验:
    [1]创建分组接口:
        public interface AddGroup {}
        public interface UpdateGroup {}
    [2]给需要校验的实体类属性添加校验注解:
        public class Student {
            @Min(value = 1, groups = {AddGroup.class})
            private int studentNo;
            @NotBlank(message = "姓名不能为空", groups = {AddGroup.class, UpdateGroup.class})
            private String name;
            @Min(value = 1,groups = {AddGroup.class, UpdateGroup.class})
            private int age;
            @Pattern(regexp = "^[0|1]$",message = "性别只能是1:男 0:女",
                groups = {AddGroup.class,UpdateGroup.class})
            private String gender;
            public Student() {}
            // get,set略
        }
    [3]使用@Validated修饰参数为包含校验属性对象:
        @Controller
        public class TestGroupController {
            // 注: 参数BindingResult对象打开,则需要在方法中处理校验异常,关闭则可交由统一异常处理方法处理
            @GetMapping("test3")
            @ResponseBody
            public String test3(@Validated(AddGroup.class) Student student,
                    BindingResult result) {
                if(result.hasErrors()){
                    StringBuilder stringBuilder = new StringBuilder();
                    // 获取校验的错误结果
                    result.getFieldErrors().forEach(item->{
                        // 获取错误的属性的名字
                        String field = item.getField();
                        // 获取到错误提示
                        String message = item.getDefaultMessage();
                        stringBuilder.append(field).append("-").append(message).append(", ");
                    });
                    return "提交的数据不合法: "+ stringBuilder.toString();
                }
                return "success";
            }
        }
        注: 
            使用分组校验后,未添加分组的属性不会进行校验;
            BindingResult对象一旦在方法参数中使用,校验异常时会自动将异常捕捉封装进该对象,不会向外抛出;
    [4]测试: http://localhost:8080/test3?studentNo=1&name=柯南&age=8&gender=1
6.自定义校验规则:
    [1]自定义校验注解的基本格式:
        @Target({ElementType.FIELD, ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Constraint(validatedBy = MyConstraintValidator.class)
        public @interface MyConstraint {
            String message();
            Class<?>[] groups() default {};
            Class<? extends Payload>[] payload() default {};
        }
        (@Constraint注解表明这个注解是用于规则校验的,validatedBy属性表明用什么去校验)
    [2]编写校验类:
        public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {
            @Override
            public void initialize(MyConstraint myConstraint) {
                System.out.println("my validator init");
            }
            @Override
            public boolean isValid(Object o,ConstraintValidatorContext constraintValidatorContext){
                // 包含字母k则校验不通过
                return !o.toString().contains("k");
            }
        }
        (自定义校验类需要实现ConstraintValidator接口,该接口必须指定两个泛型)
        (第一个泛型指的是上面定义的注解类型,第二个泛型表示校验对象的类型)
        (MyConstraintValidator实现了ConstraintValidator接口的initialize方法和isValid方法)
        (initialize方法用于该校验初始化的时候进行一些操作;
         isValid方法用于编写校验逻辑,第一个参数为需要校验的值,第二个参数为校验上下文)
    [3]测试自定义校验规则:
        @Controller
        @Validated
        public class TestCustomController {
            @GetMapping("test4")
            @ResponseBody
            public String test4(@MyConstraint(message = "{illegal}") String name){
                return "success";
            }
        }
6.使用全局异常统一处理类捕捉校验异常:
    @RestControllerAdvice
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public class GlobalExceptionHandler {
        //统一处理请求方法参数校验(只适用于普通传参)
        @ExceptionHandler(value = ConstraintViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public String handleConstraintViolationException(ConstraintViolationException e) {
            // 这里异常处理仅仅是简单地将校验异常参数和异常信息拼接后返回
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            for (ConstraintViolation<?> violation : violations) {
                Path path = violation.getPropertyPath();
                System.out.println(path.toString());
            }
            return e.getMessage();
        }
        // 统一处理请求对象属性校验(只适用于实体传参,若有其他绑定异常也会被这样处理(可能存在冲突))
        @ExceptionHandler({BindException.class})
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public String handleBindException(BindException e){
            // 这里异常处理仅仅是简单地将校验异常的实体类及其属性和异常信息拼接后返回
            StringBuilder stringBuilder = new StringBuilder();
            e.getFieldErrors().forEach(item ->{
                // 获取错误的属性的名字
                String field = item.getField();
                // 获取到错误提示
                String message = item.getDefaultMessage();
                stringBuilder.append(field).append("-").append(message).append(", ");
            });
            return stringBuilder.toString();
        }
    }
```
