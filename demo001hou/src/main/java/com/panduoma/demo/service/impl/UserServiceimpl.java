package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panduoma.demo.entity.LoginDTO;
import com.panduoma.demo.entity.LoginLog;
import com.panduoma.demo.entity.User;
import com.panduoma.demo.mapper.LoginLogMapper;
import com.panduoma.demo.mapper.UserMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private LoginLogMapper loginLogMapper;
    @Override
    public Result<?> updateUserStatus(Long userId, Integer status) {

        User user = baseMapper.selectById(userId);
        if(user == null){
            return Result.error("用户不存在");
        }

        user.setStatus(String.valueOf(status));

        baseMapper.updateById(user);
        return Result.success("状态修改成功");
    }

    @Override
    public Result<?> login(LoginDTO loginDTO) {
        System.out.println("=== 登录请求: username=" + loginDTO.getUsername()
                + ", role=" + loginDTO.getRole() + " ===");

        if (loginDTO == null
                || loginDTO.getUsername() == null || loginDTO.getUsername().isBlank()
                || loginDTO.getPassword() == null || loginDTO.getPassword().isBlank()
                || loginDTO.getRole() == null || loginDTO.getRole().isBlank()) {
            return Result.error("参数不能为空");
        }

        User user = lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername().trim())
                .one();
        System.out.println("=== 查询结果: user=" + user + " ===");
        if (user == null) {
            return Result.error("用户名不存在");
        }

        System.out.println("=== DB password=[" + user.getPassword() + "], input password=[" + loginDTO.getPassword() + "] ===");
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("密码错误");
        }

        System.out.println("=== DB role=[" + user.getRole() + "], input role=[" + loginDTO.getRole() + "] ===");
        if (!user.getRole().equals(loginDTO.getRole().trim())) {
            return Result.error("角色不匹配");
        }
        
        // 登录成功，将 status 从 0 改为 1
        user.setStatus("1");
        baseMapper.updateById(user);

        // 写入登录日志
        System.out.println("=== 登录成功: u_id=" + user.getId() + ", role=" + user.getRole() + " ===");
        LoginLog loginLog = new LoginLog();
        loginLog.setUId(user.getId());
        loginLog.setRole(user.getRole());
        loginLog.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(loginLog);

        user.setPassword(null);
        return Result.data(user);
    }

    @Override
    public Result<?> getUserById(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.data(user);
    }

    @Override
    public Result<?> logout(User userParam) {
        if (userParam == null || userParam.getId() == null) {
            return Result.error("用户ID不能为空");
        }

        User user = baseMapper.selectById(userParam.getId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 查找该用户最近一条未记录登出时间的登录日志
        LoginLog loginLog = loginLogMapper.selectOne(
                new LambdaUpdateWrapper<LoginLog>()
                        .eq(LoginLog::getUId, userParam.getId())
                        .isNull(LoginLog::getLogoutTime)
                        .orderByDesc(LoginLog::getLoginTime)
                        .last("LIMIT 1")
        );
        if (loginLog != null) {
            loginLog.setLogoutTime(LocalDateTime.now());
            loginLogMapper.updateById(loginLog);
        }

        // 重置 status 为 0
        user.setStatus("0");
        baseMapper.updateById(user);

        return Result.success("退出登录成功");
    }
}
