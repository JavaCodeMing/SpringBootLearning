package com.example.fastdfs.util;

import com.example.fastdfs.bean.FastDFSProperties;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author dengzhiming
 * @date 2020/3/6 18:15
 */
@Component
public class FastDFSUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FastFileStorageClient storageClient;
    // 项目参数配置
    @Resource
    private FastDFSProperties appConfig;

    //上传文件
    public String uploadFile(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        // 获得文件后缀名
        StorePath storePath = storageClient.uploadFile(
                file.getInputStream(),
                file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()),
                null);
        logger.info("the time for uploading file is " + (System.currentTimeMillis() - startTime) + " ms");
        return getResAccessUrl(storePath);
    }

    //将一段字符串生成一个文件上传
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream,buff.length, fileExtension,null);
        return getResAccessUrl(storePath);
    }

    // 封装图片完整URL地址
    private String getResAccessUrl(StorePath storePath) {
        String fileUrl = "http://" + appConfig.getResHost()
                + ":" + appConfig.getStoragePort() + "/" + storePath.getFullPath();
        return fileUrl;
    }

    //删除文件
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            logger.warn(e.getMessage());
        }
    }
}
