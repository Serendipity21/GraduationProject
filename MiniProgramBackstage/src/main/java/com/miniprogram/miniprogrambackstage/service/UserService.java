package com.miniprogram.miniprogrambackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miniprogram.miniprogrambackstage.entity.User;

public interface UserService extends IService<User> {

    boolean insertUser(User user);
}
