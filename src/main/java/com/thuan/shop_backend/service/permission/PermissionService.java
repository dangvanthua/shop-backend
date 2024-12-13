package com.thuan.shop_backend.service.permission;

import com.thuan.shop_backend.dto.request.permission.PermissionRequest;
import com.thuan.shop_backend.dto.response.permisson.PermissionResponse;
import com.thuan.shop_backend.entity.Permission;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final ModelMapper mapper;
    private final PermissionRepository permissionRepository;

    @Override
    public Permission createPermission(PermissionRequest permissionRequest) {

        boolean existPermission = permissionRepository.existsByName(permissionRequest.getName());

        if(existPermission) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }

        Permission permission = mapper.map(permissionRequest, Permission.class);

        return permissionRepository.save(permission);
    }

    @Override
    public List<PermissionResponse> getAllPermission() {

        List<Permission> permissions = permissionRepository.findAll();

        return permissions.stream()
                .map(PermissionResponse::fromPermission)
                .toList();
    }

    @Override
    public PermissionResponse getPermissionById(long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        return PermissionResponse.fromPermission(permission);
    }

    @Override
    public Permission updatePermission(long permissionId, PermissionRequest permissionRequest) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        permission.setName(permissionRequest.getName());
        permission.setDescription(permissionRequest.getDescription());

        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        permissionRepository.delete(permission);
    }
}
