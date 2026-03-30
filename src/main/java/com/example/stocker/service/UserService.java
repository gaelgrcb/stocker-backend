package com.example.stocker.service;

import com.example.stocker.model.User;
import com.example.stocker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Importar HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; // Importar ResponseStatusException


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public UserResponseDTO register(UserRegistrationDTO registrationData) {
        // 409 Conflict -> Activa el modal "¡Cuenta detectada!" en React
        userRepository.findByUsername(registrationData.username())
                .ifPresent(u -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
                });

        User user = new User();
        user.setName(registrationData.name());
        user.setEmail(registrationData.email());
        user.setUsername(registrationData.username());
        user.setBusiness(registrationData.business());
        user.setRole("user");

        user.setPassword(passwordEncoder.encode(registrationData.password()));

        userRepository.save(user);
        String token = tokenService.generateToken(user);

        return new UserResponseDTO(user.getUsername(), token);
    }

    public UserResponseDTO login(UserLoginDTO loginData) {
        // 404 Not Found -> Activa el modal "¿Quieres crear una cuenta?"
        User user = userRepository.findByUsername(loginData.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 401 Unauthorized -> Activa el modal "Contraseña incorrecta"
        if (!passwordEncoder.matches(loginData.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        String token = tokenService.generateToken(user);
        return new UserResponseDTO(user.getUsername(), token);
    }

    public record UserRegistrationDTO(String name, String email, String business, String username, String password) {}
    public record UserLoginDTO(String username, String password) {}
    public record UserResponseDTO(String username, String token) {}
}