package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.utils.OSSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器（使用阿里云OSS）
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class FileUploadController {
    
    @Autowired
    private OSSUtil ossUtil;
    
    /**
     * 上传头像到OSS
     */
    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 2. 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }
            
            // 3. 验证文件类型（只允许图片）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 4. 获取文件扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 验证扩展名
            if (!extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
                return Result.error("不支持的图片格式");
            }
            
            // 5. 验证文件大小（2MB）
            long maxSize = 2 * 1024 * 1024; // 2MB
            if (file.getSize() > maxSize) {
                return Result.error("图片大小不能超过2MB");
            }
            
            // 6. 上传到OSS（avatar/ 文件夹）
            String url = ossUtil.uploadFile(file, "avatar/");
            
            // 7. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", url.substring(url.lastIndexOf("/") + 1));
            
            return Result.success("上传成功", result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传音乐封面到OSS
     */
    @ApiOperation("上传音乐封面")
    @PostMapping("/cover")
    public Result<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            // 验证和上传逻辑与头像类似
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 上传到OSS（cover/ 文件夹）
            String url = ossUtil.uploadFile(file, "cover/");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", url.substring(url.lastIndexOf("/") + 1));
            
            return Result.success("上传成功", result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传音乐文件到OSS
     */
    @ApiOperation("上传音乐文件")
    @PostMapping("/music")
    public Result<Map<String, String>> uploadMusic(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("audio/")) {
                return Result.error("只能上传音频文件");
            }
            
            // 验证文件大小（30MB）
            long maxSize = 30 * 1024 * 1024; // 30MB
            if (file.getSize() > maxSize) {
                return Result.error("音频文件大小不能超过30MB");
            }
            
            // 上传到OSS（music/ 文件夹）
            String url = ossUtil.uploadFile(file, "music/");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", url.substring(url.lastIndexOf("/") + 1));
            
            return Result.success("上传成功", result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
