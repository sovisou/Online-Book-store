package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.onlinebookstore.dto.user.UserResponseDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.exception.RegistrationException;
import com.onlinebookstore.mapper.UserMapper;
import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.role.RoleRepository;
import com.onlinebookstore.repository.user.UserRepository;
import com.onlinebookstore.service.ShoppingCartService;
import com.onlinebookstore.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(String.format("User with this email: %s already exists")
                    + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findByRole(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role USER not found"))));
        user = userRepository.save(user);
        shoppingCartService.createShoppingCartForUser(user);
        return userMapper.toUserResponse(user);
    }
}
