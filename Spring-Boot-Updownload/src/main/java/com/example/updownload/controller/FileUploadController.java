package com.example.updownload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by dengzhiming on 2019/8/11
 */
@Controller
public class FileUploadController {

    private Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    //文件路径
    //private String path = "D:\\datafile\\";
    private static String path;

    static {
        String s = System.getProperty("user.dir");
        File fileDir = new File(s + "/src/main/resources/file");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        path = fileDir.getPath() + File.separator;
    }

    @GetMapping("/upload")
    public String singleFile() {
        return "upload";
    }

    @GetMapping("/uploadBatch")
    public String multipleFiles() {
        return "uploadBatch";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(MultipartFile file) {
        // 判断非空
        if (file.isEmpty()) {
            return "上传的文件不能为空";
        }
        try {
            // 测试MultipartFile接口的方法
            logger.info("[文件类型ContentType] - [{}]", file.getContentType());
            logger.info("[文件组件名称Name] - [{}]", file.getName());
            logger.info("[文件原名称OriginalFileName] - [{}]", file.getOriginalFilename());
            logger.info("[文件大小] - [{}]", file.getSize());
            logger.info("path is " + path);
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
            logger.info(path + file.getOriginalFilename());
            File dir = new File(path + file.getOriginalFilename());
            // 这里除了transferTo方法,也可以用字节流的方式上传文件,但是字节流比较慢,所以还是建议用transferTo
            file.transferTo(dir);
            // writeFile(file);
            return "上传单个文件成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "上传单个文件失败";
        }
    }

    @PostMapping("/uploadBatch")
    @ResponseBody
    public String uploadBatch(MultipartFile[] files) {
        if (files != null && files.length > 0) {
            for (MultipartFile mf : files) {
                // 获取源文件名称
                String filename = mf.getOriginalFilename();
                // 判断非空
                if (mf.isEmpty()) {
                    return "文件名称: " + filename + " 上传失败,原因是文件为空！";
                }
                File dir = new File(path + filename);
                try {
                    // 写入文件
                    mf.transferTo(dir);
                    logger.info("文件名称: " + filename + "上传成功");
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                    return "文件名称: " + filename + " 上传失败";
                }
            }
            return "多文件上传成功";
        }
        return "多文件上传失败";
    }

    private void writeFile(MultipartFile file) {
        try {
            // 获取输出流
            OutputStream os = new FileOutputStream(path + file.getOriginalFilename());
            // 获取输入流 (CommonsMultipartFile 中可以直接得到文件的流)
            InputStream is = file.getInputStream();
            byte[] buffer = new byte[1024];
            // 判断输入流中的数据是否已经读完的标识
            int length = 0;
            // 循环将输入流读入缓冲区 (len=in.read(buffer))>0就表示in里面还有数据
            while ((length = is.read(buffer)) != -1) {
                // 使用FileOutputStream输出流将缓冲区的数据写入指定目录的文件(savePath + "\\" + filename)中
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
