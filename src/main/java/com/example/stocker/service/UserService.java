package com.example.stocker.service;

import com.example.stocker.model.User;
import com.example.stocker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO register(UserRegistrationDTO registrationData) {
        if (userRepository.findByUsername(registrationData.username).isPresent()) {
            throw new RuntimeException("El usuario ya está registrado");
        }

        User user = new User();
        user.setName(registrationData.name);
        user.setEmail(registrationData.email);
        user.setUsername(registrationData.username);
        user.setRole("user");

        String encryptPass = passwordEncoder.encode(registrationData.password);
        user.setPassword(encryptPass);

        userRepository.save(user);
        return new UserResponseDTO(
                user.getUsername()
        );
    }

    public UserResponseDTO login(UserLoginDTO loginData) {
        User user = userRepository.findByUsername(loginData.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(loginData.password(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return new UserResponseDTO(
                user.getUsername()
        );
    }

    public record UserRegistrationDTO(
            String name,
            String email,
            String username,
            String password
    ) {}

    public record UserLoginDTO(
            String username,
            String password
    ) {}

    public record UserResponseDTO(
            String username
    ) {}
}