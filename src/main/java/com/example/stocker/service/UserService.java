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
    public User register(UserRegistrationDTO registrationData) {
        if (userRepository.findByUsername(registrationData.username).isPresent()) {
            throw new RuntimeException("El usuario ya está registrado");
        }

        User user = new User();
        user.setName(registrationData.name);
        user.setEmail(registrationData.email);
        user.setUsername(registrationData.username);

        String encryptPass = passwordEncoder.encode(registrationData.password);
        user.setPassword(encryptPass);

        return userRepository.save(user);
    }

    public User login(String username, String password){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Contraseña incorrecta");
        }

        return user;
    }

    public record UserRegistrationDTO(
            String name,
            String email,
            String username,
            String password
    ) {}
}