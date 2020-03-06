package com.example.fastdfs.controller;

import com.example.fastdfs.util.FastDFSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * @author dengzhiming
 * @date 2020/3/6 18:12
 */
@Controller
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private FastDFSUtil dfsClient;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile
                ,RedirectAttributes redirectAttributes){
        if (multipartFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        // 将图片或者音视频上传到分布式的文件存储系统
        // 将图片的存储路径返回给页面
        try {
            String path = dfsClient.uploadFile(multipartFile);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + multipartFile.getOriginalFilename() + "'");
            redirectAttributes.addFlashAttribute("path",
                    "file path url '" + path + "'");
        } catch (IOException e) {
            logger.error("upload file failed",e);
        }
        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }
}
