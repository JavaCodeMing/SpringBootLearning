```text
1.引入web依赖和thymeleaf依赖:
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
2.编写全局配置文件:(application.properties)
    # thymeleaf
    spring.thymeleaf.prefix=classpath:/templates/
    spring.thymeleaf.suffix=.html
    spring.thymeleaf.mode=HTML
    spring.thymeleaf.encoding=UTF-8
    spring.thymeleaf.servlet.content-type=text/html
    spring.thymeleaf.cache=false
    # 上传文件总的最大值
    spring.servlet.multipart.max-request-size=100MB
    # 单个文件的最大值
    spring.servlet.multipart.max-file-size=100MB
    # 是否支持批量上传(默认值 true)
    spring.servlet.multipart.enabled=true
    # 上传文件的临时目录(一般情况下不用特意修改)
    #spring.servlet.multipart.location=
    # 文件大小阈值,当大于这个阈值时将写入到磁盘,否则存在内存中(默认值0)
    spring.servlet.multipart.file-size-threshold=0
    # 判断是否要延迟解析文件(相当于懒加载,一般情况下不用特意修改)
    spring.servlet.multipart.resolve-lazily=false
3.创建上传下载的目录: \src\main\resources\file\
4.编写Controller测试上传:
    @Controller
    public class FileUploadController {
        private Logger logger = LoggerFactory.getLogger(FileUploadController.class);
        //文件路径
        //private String path = "D:\\datafile\\";
        private String path;
        {
            try {
                path = new ClassPathResource("/file/").getFile().getPath() + java.io.File.separator;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @GetMapping("/upload")
        public String singleFile() {return "upload";}
        @GetMapping("/uploadBatch")
        public String multipleFiles() {return "uploadBatch";}
        @PostMapping("/upload")
        @ResponseBody
        public String upload(MultipartFile file){
            // 判断非空
            if(file.isEmpty()){return "上传的文件不能为空";}
            try {
                // 测试MultipartFile接口的方法
                logger.info("[文件类型ContentType] - [{}]",file.getContentType());
                logger.info("[文件组件名称Name] - [{}]",file.getName());
                logger.info("[文件原名称OriginalFileName] - [{}]",file.getOriginalFilename());
                logger.info("[文件大小] - [{}]",file.getSize());
                logger.info("path is "+path);
                File f = new File(path);
                if(!f.exists()){f.mkdir();}
                logger.info(path + file.getOriginalFilename());
                File dir = new File(path + file.getOriginalFilename());
                // 除了transferTo方法,也可用字节流的方式上传文件,但是字节流比较慢(建议用transferTo)
                file.transferTo(dir);
                // writeFile(file);
                return "上传单个文件成功";
            } catch (Exception e){
                e.printStackTrace();
                return "上传单个文件失败";
            }
        }
        @PostMapping("/uploadBatch")
        @ResponseBody
        public String uploadBatch(MultipartFile[] files){
            if(files != null && files.length > 0){
                for (MultipartFile mf : files) {
                    // 获取源文件名称
                    String filename = mf.getOriginalFilename();
                    // 判断非空
                    if(mf.isEmpty()){ return "文件名称: "+ filename+ " 上传失败,原因是文件为空！"; }
                    File dir = new File(path + filename);
                    try {
                        // 写入文件
                        mf.transferTo(dir);
                        logger.info("文件名称: " + filename + "上传成功");
                    }catch (Exception e){
                        logger.error(e.toString(),e);
                        return "文件名称: "+ filename + " 上传失败";
                    }
                }
                return "多文件上传成功";
            }
            return "多文件上传失败";
        }
        private void writeFile(MultipartFile file){
            try {
                // 获取输出流
                OutputStream os = new FileOutputStream(path + file.getOriginalFilename());
                // 获取输入流 (CommonsMultipartFile 中可以直接得到文件的流)
                InputStream is = file.getInputStream();
                byte[] buffer = new byte[1024];
                // 判断输入流中的数据是否已经读完的标识
                int length = 0;
                // 循环将输入流读入缓冲区 (len=in.read(buffer))>0就表示in里面还有数据
                while ((length = is.read(buffer)) != -1){
                    // 使用输出流将缓冲区的数据写入指定目录的文件(savePath+"\\"+filename)中
                    os.write(buffer,0,length);
                }
                os.flush();
                os.close();
                is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
5.编写简单的上传页面:
    [1]单文件上传: (upload.html)
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <title>单文件上传</title>
        </head>
        <body>
            <p>单文件上传</p>
            <form method="post" enctype="multipart/form-data" action="/upload">
                文件: <input type="file" name="file"/>
                <input type="submit"/>
            </form>
        </body>
        </html>
    [2]多文件上传: (uploadBatch.html)
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>多文件上传</title>
        </head>
        <body>
            <p>多文件上传</p>
            <form method="post" enctype="multipart/form-data" action="/uploadBatch">
                <p>文件1: <input type="file" name="files"/></p>
                <p>文件2: <input type="file" name="files"/></p>
                <p><input type="submit" value="上传"/></p>
            </form>
        </body>
        </html>
6.编写Controller测试下载:
    @Controller
    public class FileDownloadController {
        private Logger logger = LoggerFactory.getLogger(FileDownloadController.class);
        @GetMapping("/download")
        public String download(){ return "download"; }
        @GetMapping("/downloadfile")
        @ResponseBody
        public String downloadfile(@RequestParam String fileName,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
            if(fileName != null && !"".equals(fileName)){
                Resource resource;
                String file;
                try {
                    // 获取完整的文件名
                    resource = new ClassPathResource("/file/"+fileName);
                    file = resource.getFile().getPath();
                }catch (Exception e){
                    e.printStackTrace();
                    return "文件不存在";
                }
                File f = new File(file);
                if(f.exists()){
                    // 设置响应对象
                    response.setContentType("multipart/form-data");
                    response.addHeader("Content-Disposition","attachment; fileName="
                        + fileName +";filename*=utf-8 "+ URLEncoder.encode(fileName,"UTF-8"));
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    try {
                        fis = new FileInputStream(f);
                        bis = new BufferedInputStream(fis);
                        ServletOutputStream os = response.getOutputStream();
                        int len = bis.read(buffer);
                        logger.info("the size of buffer is "+ len);
                        while (len != -1) {
                            os.write(buffer,0, len);
                            len = bis.read(buffer);
                        }
                        return "下载成功";
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(bis != null){
                            try {
                                bis.close();
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        if(fis != null){
                            try {
                                fis.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return "文件不存在";
        }
    }
7.编写简单的下载页面: (download.html)
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>文件下载</title>
        <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    </head>
    <body>
        <label for="fileName">下载的文件名：</label><input id="fileName" type="text"/>
        <a href="javascript:void(0);" onclick="down()">下载文件</a>
        <a id="download" style="display:none"></a>
    <script>
        function down() {
            var fileName = $("#fileName").val().toString();
            console.log(fileName);
            var $a = document.getElementById("download");
            $a.setAttribute("href","/downloadfile?fileName="+fileName);
            $a.click();
        }
    </script>
    </body>
    </html>
```