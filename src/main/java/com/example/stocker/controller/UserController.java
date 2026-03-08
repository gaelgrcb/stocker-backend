package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public User register(@RequestBody UserService.UserRegistrationDTO dto) {
        return userService.register(dto);
    }
}
