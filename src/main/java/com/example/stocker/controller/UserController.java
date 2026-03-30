package com.example.stocker.controller;

import com.example.stocker.model.User;
import com.example.stocker.repository.UserRepository;
import com.example.stocker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // ¡Súper importante inyectar esto aquí!

    @PostMapping("/register")
    public ResponseEntity<UserService.UserResponseDTO> register(@RequestBody UserService.UserRegistrationDTO dto) {
        UserService.UserResponseDTO response = userService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserService.UserResponseDTO> login(@RequestBody UserService.UserLoginDTO dto) {
        UserService.UserResponseDTO response = userService.login(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getProfile(Authentication authentication) {
        // Extraemos lo que sea que Spring haya guardado
        Object principal = authentication.getPrincipal();
        User user;

        // Caso 1: Spring guardó el objeto User completo (Lo ideal)
        if (principal instanceof User) {
            user = (User) principal;
        }
        // Caso 2: Spring guardó solo el username como texto (Pasa muy seguido)
        else if (principal instanceof String) {
            String username = (String) principal;
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la DB"));
        }
        // Caso 3: Fallo de seguridad
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Ya tenemos el usuario seguro, mandamos la respuesta
        return ResponseEntity.ok(new UserProfileDTO(
                user.getName(),
                user.getUsername(),
                user.getBusiness()
        ));
    }

    public record UserProfileDTO(String name, String username, String business) {}
}