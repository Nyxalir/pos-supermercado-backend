package com.pos.supermercado.controller;

import com.pos.supermercado.model.Admin;
import com.pos.supermercado.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Valida si una identificación o nombre de usuario corresponde a un Administrador del sistema.
     * Retorna { esAdmin: true/false, username: string }
     */
    @GetMapping("/verificar/{usuario}")
    public ResponseEntity<Map<String, Object>> verificarUsuario(@PathVariable String usuario) {
        boolean esAdmin = adminRepository.existsByUsername(usuario.trim());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("esAdmin", esAdmin);
        respuesta.put("username", usuario.trim());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Alternativa POST para verificar si es usuario admin
     */
    @PostMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarUsuarioPost(@RequestBody Map<String, String> body) {
        String usuario = body.getOrDefault("username", body.getOrDefault("usuario", "")).trim();
        boolean esAdmin = adminRepository.existsByUsername(usuario);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("esAdmin", esAdmin);
        respuesta.put("username", usuario);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Autenticación de Administrador
     * Valida usuario y contraseña contra la tabla 'admin' usando BCrypt
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.getOrDefault("username", "").trim();
        String password = credentials.getOrDefault("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Usuario y contraseña son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Admin admin = adminOpt.get();
        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Contraseña incorrecta");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Generar token Basic Auth compatible con Spring Security HttpBasic
        String basicToken = "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", basicToken);
        response.put("username", admin.getUsername());
        response.put("rol", "ADMIN");
        return ResponseEntity.ok(response);
    }
}
