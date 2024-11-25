package com.thuan.shop_backend.service.role;

import com.thuan.shop_backend.dto.request.RoleRequest;
import com.thuan.shop_backend.dto.response.RoleResponse;
import com.thuan.shop_backend.entity.Permission;
import com.thuan.shop_backend.entity.Role;
import com.thuan.shop_backend.entity.RolePermission;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.PermissionRepository;
import com.thuan.shop_backend.repository.RolePermissionRepository;
import com.thuan.shop_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());

        if(permissions.isEmpty() || permissions.size() != request.getPermissionIds().size()) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        role = roleRepository.save(role);

        List<RolePermission> rolePermissions = new ArrayList<>();

        for(Permission permission : permissions) {
            var newRolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            rolePermissions.add(newRolePermission);
        }

        rolePermissionRepository.saveAll(rolePermissions);

        return RoleResponse.fromRole(role, permissions);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role ->  {

                    List<Permission> permissions = rolePermissionRepository
                            .findPermissionByRoleId(role.getId());

                    return RoleResponse.fromRole(role, permissions);
                })
                .toList();
    }

    @Override
    public RoleResponse getRole(long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        List<Permission> permissions = rolePermissionRepository.findPermissionByRoleId(role.getId());

        return RoleResponse.fromRole(role, permissions);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(long roleId, RoleRequest roleRequest) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if (roleRequest.getName() != null && !roleRequest.getName().isEmpty()) {
            role.setName(roleRequest.getName());
        }

        if (roleRequest.getDescription() != null && !roleRequest.getDescription().isEmpty()) {
            role.setDescription(roleRequest.getDescription());
        }

        if (roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissionIds());

            if (permissions.isEmpty() || permissions.size() != roleRequest.getPermissionIds().size()) {
                throw new AppException(ErrorCode.INVALID_PERMISSION);
            }

            rolePermissionRepository.deleteAllByRoleId(roleId);

            List<RolePermission> rolePermissions = new ArrayList<>();
            for (Permission permission : permissions) {
                var newRolePermission = RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build();
                rolePermissions.add(newRolePermission);
            }

            rolePermissionRepository.saveAll(rolePermissions);
        }

        role = roleRepository.save(role);

        List<Permission> updatedPermissions = rolePermissionRepository.findPermissionByRoleId(roleId);

        return RoleResponse.fromRole(role, updatedPermissions);
    }

    @Override
    @Transactional
    public void deleteRole(long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        roleRepository.delete(role);
    }
}