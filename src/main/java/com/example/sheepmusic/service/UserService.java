package com.example.sheepmusic.service;

import com.example.sheepmusic.dto.LoginRequest;
import com.example.sheepmusic.dto.RegisterRequest;
import com.example.sheepmusic.dto.UpdatePasswordRequest;
import com.example.sheepmusic.dto.UpdateUserRequest;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.repository.UserRepository;
import com.example.sheepmusic.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务层
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("邮箱已被注册");
            }
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // 加密密码
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(1);  // 正常状态
        
        // 如果用户名是 admin，自动设为管理员
        if ("admin".equals(request.getUsername())) {
            user.setRole("admin");
        } else {
            user.setRole("user");
        }
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账号状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成Token（包含角色）
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", getUserInfo(user));
        
        return result;
    }
    
    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    /**
     * 获取用户信息（脱敏，不返回密码）
     */
    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("email", user.getEmail());
        info.put("phone", user.getPhone());
        info.put("gender", user.getGender());
        info.put("signature", user.getSignature());
        info.put("role", user.getRole());  // 添加角色
        info.put("createdAt", user.getCreateTime());  // 使用 createTime 字段
        return info;
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(Long userId, UpdateUserRequest request) {
        // 查找用户
        User user = getUserById(userId);
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            User existUser = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new RuntimeException("该邮箱已被其他用户使用");
            }
        }
        
        // 更新用户信息
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail().isEmpty() ? null : request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar().isEmpty() ? null : request.getAvatar());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * 修改密码
     */
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 查找用户
        User user = getUserById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 检查新密码不能与旧密码相同
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    /**
     * 获取用户信息Map（公开方法，用于Controller）
     */
    public Map<String, Object> getUserInfoMap(User user) {
        return getUserInfo(user);
    }
}

