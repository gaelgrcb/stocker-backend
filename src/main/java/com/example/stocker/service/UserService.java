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
        if (userRepository.findByUsername(registrationData.username).isPresent()) {
            // Cambiado a ResponseStatusException para que el frontend reciba un 409 (Conflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        User user = new User();
        user.setName(registrationData.name);
        user.setEmail(registrationData.email);
        user.setUsername(registrationData.username);
        user.setBussines(registrationData.bussines);
        user.setRole("user");

        String encryptPass = passwordEncoder.encode(registrationData.password);
        user.setPassword(encryptPass);

        userRepository.save(user);
        String token = tokenService.generateToken(user);

        return new UserResponseDTO(user.getUsername(), token);
    }

    public UserResponseDTO login(UserLoginDTO loginData) {
        //Lanzar 404 si el usuario no existe
        User user = userRepository.findByUsername(loginData.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        //Lanzar 401 si la contraseña es incorrecta
        if (!passwordEncoder.matches(loginData.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        String token = tokenService.generateToken(user);

        return new UserResponseDTO(user.getUsername(), token);
    }

    public record UserRegistrationDTO(
            String name,
            String email,
            String bussines,
            String username,
            String password
    ) {}

    public record UserLoginDTO(
            String username,
            String password
    ) {}

    public record UserResponseDTO(
            String username,
            String token
    ) {}
}