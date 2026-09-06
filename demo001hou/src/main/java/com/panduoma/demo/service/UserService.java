package com.panduoma.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panduoma.demo.entity.LoginDTO;
import com.panduoma.demo.entity.User;
import com.panduoma.demo.response.Result;

import java.util.Map;

public interface UserService extends IService<User> {
    Result<?> login(LoginDTO loginDTO);
    Result<?> updateUserStatus(Long userId, Integer status);
    Result<?> getUserById(Long userId);
    Result<?> logout(User user);
    Result<?> systemUsers();
    Result<?> systemUserCreate(Map<String, Object> request);
}
