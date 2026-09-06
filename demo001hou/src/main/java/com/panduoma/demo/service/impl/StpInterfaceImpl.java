package com.panduoma.demo.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panduoma.demo.entity.Permission;
import com.panduoma.demo.entity.UserPermission;
import com.panduoma.demo.mapper.PermissionMapper;
import com.panduoma.demo.mapper.UserPermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserPermissionMapper userPermissionMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     * 通过 user_permission 关联表查询用户对应的权限点编码
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        long userId;
        try {
            userId = Long.parseLong(loginId.toString());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        // 1. 查询 user_permission 表，获取该用户关联的所有 permission_id
        List<UserPermission> userPermissions = userPermissionMapper.selectList(
                new LambdaQueryWrapper<UserPermission>()
                        .eq(UserPermission::getUserId, userId)
        );
        if (userPermissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取 permission_id 列表
        List<Integer> permissionIds = userPermissions.stream()
                .map(UserPermission::getPermissionId)
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
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 本 list 仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        List<String> list = new ArrayList<String>();
        list.add("admin");
        list.add("super-admin");
        return list;
    }

}