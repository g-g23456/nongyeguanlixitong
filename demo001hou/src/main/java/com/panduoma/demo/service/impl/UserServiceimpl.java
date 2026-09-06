package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panduoma.demo.entity.LoginDTO;
import com.panduoma.demo.entity.LoginLog;
import com.panduoma.demo.entity.User;
import com.panduoma.demo.mapper.LoginLogMapper;
import com.panduoma.demo.mapper.PermissionMapper;
import com.panduoma.demo.mapper.UserMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceimpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Resource
    private PermissionMapper permissionMapper;

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

    @Override
    public Result<?> systemUsers() {
        // 1. 统计数据
        long totalUsers = baseMapper.selectCount(null);
        long totalPermissions = permissionMapper.selectCount(null);
        long totalLogs = loginLogMapper.selectCount(null);

        // 2. 查询所有用户，隐藏密码
        List<User> users = baseMapper.selectList(null);
        for (User user : users) {
            user.setPassword(null);
        }

        // 3. 组装响应
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalRoles", 4);
        stats.put("totalPermissions", totalPermissions);
        stats.put("totalLogs", totalLogs);

        Map<String, Object> data = new HashMap<>();
        data.put("stats", stats);
        data.put("list", users);

        return Result.data(data);
    }

    @Override
    public Result<?> systemUserCreate(Map<String, Object> request) {
        if (request == null) {
            return Result.error("参数不能为空");
        }

        String username = request.get("username") != null ? request.get("username").toString() : null;
        String name = request.get("name") != null ? request.get("name").toString() : null;
        String password = request.get("password") != null ? request.get("password").toString() : null;
        String role = request.get("role") != null ? request.get("role").toString() : null;
        String department = request.get("department") != null ? request.get("department").toString() : null;
        Object statusObj = request.get("status");
        String status = statusObj != null ? statusObj.toString() : "0";

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.error("用户名和密码不能为空");
        }

        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        user.setDepartment(department);
        user.setStatus(status);

        baseMapper.insert(user);
        return Result.success("创建成功");
    }
}
