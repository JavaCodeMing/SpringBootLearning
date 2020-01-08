package com.example.updownload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by dengzhiming on 2019/8/11
 */
@Controller
public class FileDownloadController {

    private Logger logger = LoggerFactory.getLogger(FileDownloadController.class);

    @GetMapping("/download")
    public String download() {
        return "download";
    }

    @GetMapping("/downloadfile")
    @ResponseBody
    public String downloadfile(@RequestParam String fileName,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        if (fileName != null && !"".equals(fileName)) {
            Resource resource;
            String file;
            try {
                // 获取完整的文件名
                resource = new ClassPathResource("/file/" + fileName);
                file = resource.getFile().getPath().trim();
            } catch (Exception e) {
                e.printStackTrace();
                return "文件不存在";
            }
            File f = new File(file);
            if (f.exists()) {
                // 设置响应对象
                response.setContentType("multipart/form-data");
                response.addHeader("Content-Disposition", "attachment; fileName="
                        + fileName + ";filename*=utf-8 " + URLEncoder.encode(fileName, "UTF-8"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(f);
                    bis = new BufferedInputStream(fis);
                    ServletOutputStream os = response.getOutputStream();
                    int len = bis.read(buffer);
                    logger.info("the size of buffer is " + len);
                    while (len != -1) {
                        os.write(buffer, 0, len);
                        len = bis.read(buffer);
                    }
                    return "下载成功";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    logger.info("下载耗时" + (System.currentTimeMillis() - start) + "ms");
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "文件不存在";
    }

    @GetMapping("/downloadfile1")
    @ResponseBody
    public String downloadfile1(@RequestParam String fileName,
                                HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        if (fileName != null && !"".equals(fileName)) {
            Resource resource;
            String file;
            try {
                // 获取完整的文件名
                resource = new ClassPathResource("/file/" + fileName);
                file = resource.getFile().getPath().trim();
            } catch (Exception e) {
                e.printStackTrace();
                return "文件不存在";
            }
            File f = new File(file);
            if (f.exists()) {
                // 设置编码
                response.setCharacterEncoding("UTF-8");
                // 设置文件格式
                response.setContentType("application/xml; charset=utf-8");
                // 设置文件名，解决乱码
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                // 设置缓冲区大小
                int bufferSize = 8192;
                int readSize = 0;
                int writeSize = 0;
                // 获取文件流
                FileInputStream fileInputStream = new FileInputStream(f);
                FileChannel fileChannel = fileInputStream.getChannel();
                // allocateDirect速度更快
                ByteBuffer buff = ByteBuffer.allocateDirect(bufferSize);
                try {
                    while ((readSize = fileChannel.read(buff)) != -1) {
                        if (readSize == 0) {
                            continue;
                        }
                        buff.position(0);
                        buff.limit(readSize);
                        while (buff.hasRemaining()) {
                            writeSize = Math.min(buff.remaining(), bufferSize);
                            byte[] byteArr = new byte[writeSize];
                            buff.get(byteArr, 0, writeSize);
                            response.getOutputStream().write(byteArr);
                        }
                        buff.clear();
                    }
                    return "下载成功";
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    logger.info("下载耗时" + (System.currentTimeMillis() - start) + "ms");
                    buff.clear();
                    fileChannel.close();
                    fileInputStream.close();
                }
            }
        }
        return "文件不存在";
    }
}
