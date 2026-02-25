package com.zenith.userservice.controller;

import com.zenith.userservice.dto.UpdateProfileRequest;
import com.zenith.userservice.dto.UserResponse;
import com.zenith.userservice.security.CurrentUser;
import com.zenith.userservice.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        CurrentUser currentUser = CurrentUser.from(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponse user = userService.getById(currentUser.getUserId());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {
        CurrentUser currentUser = CurrentUser.from(authentication);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponse user = userService.updateProfile(currentUser.getUserId(), request);
        return ResponseEntity.ok(user);
    }
}
