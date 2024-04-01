package com.miniprogram.miniprogrambackstage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("user")
public class User {
    @TableId
    private String openid;
    private String name;
    private String avatar;
    private String college;
    private String major;
    private String grade;
}
