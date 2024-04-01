package com.miniprogram.miniprogrambackstage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miniprogram.miniprogrambackstage.entity.User;
import com.miniprogram.miniprogrambackstage.mapper.UserMapper;
import com.miniprogram.miniprogrambackstage.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean insertUser(User user) {
        return save(user);
    }
}
