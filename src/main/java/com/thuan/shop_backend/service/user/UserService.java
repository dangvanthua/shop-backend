package com.thuan.shop_backend.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.PredefinedRole;
import com.thuan.shop_backend.constant.ProviderType;
import com.thuan.shop_backend.dto.request.user.UserCreateRequest;
import com.thuan.shop_backend.dto.request.user.UserLoginRequest;
import com.thuan.shop_backend.dto.response.role.RoleResponse;
import com.thuan.shop_backend.dto.response.user.UserResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.RoleRepository;
import com.thuan.shop_backend.repository.SocialAccountRepository;
import com.thuan.shop_backend.repository.UserRepository;
import com.thuan.shop_backend.repository.UserRoleRepository;
import com.thuan.shop_backend.service.file.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final IFileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final AuthComponent authComponent;

    @Override
    @Transactional
    public User createUser(UserCreateRequest userCreateRequest) {

        validateUserUniqueness(userCreateRequest.getEmail(), userCreateRequest.getPhoneNumber());

        Role role = roleRepository.findByName(PredefinedRole.USER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .fullName(userCreateRequest.getFullName())
                .phoneNumber(userCreateRequest.getPhoneNumber())
                .email(userCreateRequest.getEmail())
                .password(passwordEncoder.encode(userCreateRequest.getPassword()))
                .dateOfBirth(userCreateRequest.getDateOfBirth())
                .isActive(true)
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

        return user;
    }

    @Override
    @Transactional
    public User createUserSocialAccount(UserLoginRequest userLoginRequest) {

        Optional<User> userOptional = userRepository.findByEmail(userLoginRequest.getEmail());

        if(userOptional.isPresent()) {
            return userOptional.get();
        }

        Role role = roleRepository.findByName(PredefinedRole.USER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .fullName(userLoginRequest.getFullName())
                .email(userLoginRequest.getEmail())
                .isActive(true)
                .build();

        if(userLoginRequest.getPicture() != null
                && userLoginRequest.getPicture().length() <= 255) {
            user.setAvatar(userLoginRequest.getPicture());
        }
        user = userRepository.save(user);

        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());

        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .role(role)
                .user(user)
                .build();
        userRole = userRoleRepository.save(userRole);

        user.setRole(List.of(userRole));

        if(socialAccountRepository.existsByProviderId(userLoginRequest.getProviderId())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String providerName = "";

        if(userLoginRequest.getProviderName()
                .equals(ProviderType.GOOGLE.name().toLowerCase())) {
            providerName = ProviderType.GOOGLE.name();
        } else if (userLoginRequest.getProviderName()
                .equals(ProviderType.FACEBOOK.name().toLowerCase())) {
            providerName = ProviderType.FACEBOOK.name();
        }

        SocialAccount socialAccount = SocialAccount.builder()
                .provider(providerName)
                .providerId(userLoginRequest.getProviderId())
                .user(user)
                .build();

        socialAccountRepository.save(socialAccount);

        return user;
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
    public User updateUser(
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

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(false);
        userRepository.save(user);
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

        if(!user.getAvatar().isEmpty()) {
            fileService.deleteFile(user.getAvatarPublicId());
        }

        user.setAvatar(avatarUrl);
        user.setAvatarPublicId(publicId);

        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public UserResponse getUserDetail() {
        String email = authComponent.getEmailFromAuthentication();
        User user = getUserByEmail(email);
        return UserResponse.fromUser(user);
    }

    @Override
    public List<UserResponse> getSellerByUserId() {
        String email = authComponent.getEmailFromAuthentication();
        User user = getUserByEmail(email);

        // get info seller that current user bought
        List<Object[]> results = userRepository.getSellerUsersForBuyer(user.getId());

        List<UserResponse> userResponses = new ArrayList<>();
        for (Object[] result : results) {

            RoleResponse roleResponse = RoleResponse.builder()
                    .id((Long) result[9])
                    .name((String) result[10])
                    .description((String) result[11])
                    .build();

            LocalDate dateOfBirth = ((java.sql.Date) result[5]).toLocalDate();
            LocalDateTime createdAt = ((java.sql.Timestamp) result[7]).toLocalDateTime();
            LocalDateTime updatedAt = ((java.sql.Timestamp) result[8]).toLocalDateTime();

            UserResponse userResponse = UserResponse.builder()
                    .id(((Integer) result[0]).longValue())
                    .fullName((String) result[1])
                    .phoneNumber((String) result[2])
                    .email((String) result[3])
                    .isActive((Boolean) result[4])
                    .dateOfBirth(dateOfBirth)
                    .avatar((String) result[6])
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .roleResponses(List.of(roleResponse))
                    .build();

            userResponses.add(userResponse);
        }

        return userResponses;
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
