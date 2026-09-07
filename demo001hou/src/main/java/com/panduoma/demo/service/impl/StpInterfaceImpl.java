package com.panduoma.demo.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panduoma.demo.entity.Permission;
import com.panduoma.demo.entity.User;
import com.panduoma.demo.entity.UserPermission;
import com.panduoma.demo.mapper.PermissionMapper;
import com.panduoma.demo.mapper.UserMapper;
import com.panduoma.demo.mapper.UserPermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义权限加载接口实现类
 * 通过 user_data → user_permissions → permissions 三表关联查询用户权限和角色
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     * 查询链路：loginId → user_permissions 表获取 permission_id → permissions 表获取 code
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        long userId;
        try {
            userId = Long.parseLong(loginId.toString());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        // 1. 查询 user_permissions 表，获取该用户关联的所有 permission_id
        List<UserPermission> userPermissions = userPermissionMapper.selectList(
                new LambdaQueryWrapper<UserPermission>()
                        .eq(UserPermission::getUserId, userId)
        );
        if (userPermissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取 permission_id 列表
        List<Long> permissionIds = userPermissions.stream()
                .map(UserPermission::getPermissionId)
                .map(Integer::longValue)
                .collect(Collectors.toList());

        // 3. 查询 permissions 表，获取对应的权限点编码
        List<Permission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .in(Permission::getId, permissionIds)
        );

        // 4. 返回权限码（code）列表
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     * 从 user_data 表查询用户的 role 字段
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        long userId;
        try {
            userId = Long.parseLong(loginId.toString());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() == null) {
            return new ArrayList<>();
        }

        return Collections.singletonList(user.getRole());
    }
}
