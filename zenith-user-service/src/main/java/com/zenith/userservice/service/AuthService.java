package com.zenith.userservice.service;

import com.zenith.userservice.dto.LoginRequest;
import com.zenith.userservice.dto.LoginResponse;
import com.zenith.userservice.dto.RegisterRequest;
import com.zenith.userservice.dto.UserResponse;
import com.zenith.userservice.entity.User;
import com.zenith.userservice.exception.BadCredentialsException;
import com.zenith.userservice.exception.EmailAlreadyExistsException;
import com.zenith.userservice.repository.UserRepository;
import com.zenith.userservice.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setUserType(request.getUserType());

        user = userRepository.save(user);
        return UserResponse.from(user);
    }



    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(BadCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getUserType());
        return new LoginResponse(token, UserResponse.from(user));
    }
}
