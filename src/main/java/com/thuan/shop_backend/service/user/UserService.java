package com.thuan.shop_backend.service.user;

import com.thuan.shop_backend.constant.PredefinedRole;
import com.thuan.shop_backend.dto.request.UserCreateRequest;
import com.thuan.shop_backend.dto.response.UserResponse;
import com.thuan.shop_backend.entity.Role;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.entity.UserRole;
import com.thuan.shop_backend.entity.UserRoleId;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.RoleRepository;
import com.thuan.shop_backend.repository.UserRepository;
import com.thuan.shop_backend.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest userCreateRequest) {

        validateUserUniqueness(userCreateRequest.getEmail(), userCreateRequest.getPhoneNumber());

        Role role = roleRepository.findByName(PredefinedRole.USER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .fullName(userCreateRequest.getFullName())
                .phoneNumber(userCreateRequest.getPhoneNumber())
                .email(userCreateRequest.getEmail())
                .password(passwordEncoder.encode(userCreateRequest.getPassword()))
                .dateOfBirth(userCreateRequest.getDateOfBirth())
                .isActive(false)
                .build();

        user = userRepository.save(user);

        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());

        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .role(role)
                .user(user)
                .build();

        userRole = userRoleRepository.save(userRole);
        user.setRole(List.of(userRole));

        return UserResponse.fromUser(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @Override
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(
            long userId,
            UserCreateRequest userCreateRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (userCreateRequest.getFullName() != null) {
            user.setFullName(userCreateRequest.getFullName());
        }

        if (userCreateRequest.getPhoneNumber() != null) {
            if (userRepository.existsByPhoneNumber(userCreateRequest.getPhoneNumber())
                    && !user.getPhoneNumber().equals(userCreateRequest.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
            }
            user.setPhoneNumber(userCreateRequest.getPhoneNumber());
        }

        if (userCreateRequest.getEmail() != null) {
            if (userRepository.existsByEmail(userCreateRequest.getEmail())
                    && !user.getEmail().equals(userCreateRequest.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setEmail(userCreateRequest.getEmail());
        }

        if (userCreateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        }

        if (userCreateRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userCreateRequest.getDateOfBirth());
        }

        user = userRepository.save(user);

        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void uploadAvatarUser(long userId, String publicId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if(avatarUrl.isEmpty() || publicId.isEmpty()) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        user.setAvatar(avatarUrl);
        user.setAvatarPublicId(publicId);

        userRepository.save(user);
    }

    private void validateUserUniqueness(String email, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
    }
}
