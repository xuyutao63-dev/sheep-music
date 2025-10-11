package com.example.sheepmusic.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
@Component
public class OSSUtil {
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.urlPrefix}")
    private String urlPrefix;
    
    /**
     * 上传文件到OSS
     * @param file 文件
     * @param folder 文件夹路径（如：avatar/）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 打印配置信息（调试用）
        System.out.println("OSS配置 - endpoint: " + endpoint);
        System.out.println("OSS配置 - bucketName: " + bucketName);
        System.out.println("OSS配置 - accessKeyId: " + accessKeyId);
        System.out.println("OSS配置 - urlPrefix: " + urlPrefix);
        
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }
        
        // 2. 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 3. 生成唯一文件名：folder + UUID + 扩展名
        String fileName = folder + UUID.randomUUID().toString() + extension;
        
        // 4. 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 5. 获取文件输入流
            InputStream inputStream = file.getInputStream();
            
            // 6. 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);
            
            // 7. 上传文件
            ossClient.putObject(putObjectRequest);
            
            // 8. 返回文件访问URL
            return urlPrefix + fileName;
            
        } finally {
            // 9. 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 删除OSS文件
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        // 从URL中提取文件路径
        String fileName = fileUrl.replace(urlPrefix, "");
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 删除文件
            ossClient.deleteObject(bucketName, fileName);
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

