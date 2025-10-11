package com.example.sheepmusic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 更新用户信息请求
 */
@Data
@ApiModel("更新用户信息请求")
public class UpdateUserRequest {
    
    @ApiModelProperty("昵称")
    @Size(min = 1, max = 50, message = "昵称长度为1-50个字符")
    private String nickname;
    
    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty("头像URL")
    private String avatar;
}

