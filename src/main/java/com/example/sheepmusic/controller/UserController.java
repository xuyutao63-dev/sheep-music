package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.dto.UpdatePasswordRequest;
import com.example.sheepmusic.dto.UpdateUserRequest;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.service.UserService;
import com.example.sheepmusic.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        try {
            // 从请求头获取Token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从Token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);
            
            // 清空密码
            user.setPassword(null);
            
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/update")
    public Result<Map<String, Object>> updateUser(@Valid @RequestBody UpdateUserRequest updateRequest, 
                                                    HttpServletRequest request) {
        try {
            // 从请求头获取Token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从Token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 更新用户信息
            User updatedUser = userService.updateUser(userId, updateRequest);
            
            // 返回更新后的用户信息
            Map<String, Object> userInfo = userService.getUserInfoMap(updatedUser);
            return Result.success("更新成功", userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest,
                                          HttpServletRequest request) {
        try {
            // 从请求头获取Token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从Token中获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 修改密码
            userService.updatePassword(userId, passwordRequest);
            
            Result<Void> result = Result.success();
            result.setMessage("密码修改成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

