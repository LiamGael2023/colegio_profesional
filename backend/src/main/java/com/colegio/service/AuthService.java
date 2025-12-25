package com.colegio.service;

import com.colegio.dto.AuthRequest;
import com.colegio.dto.AuthResponse;
import com.colegio.model.entity.Usuario;
import com.colegio.repository.UsuarioRepository;
import com.colegio.security.CustomUserDetailsService;
import com.colegio.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;

    @Value("${security.jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Autentica un usuario y genera un token JWT
     */
    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        // Autenticar credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Cargar detalles del usuario
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = userDetailsService.getUsuarioEntity(request.getUsername());

        // Actualizar último acceso
        usuario.actualizarUltimoAcceso();
        usuarioRepository.save(usuario);

        // Generar token con información adicional
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("rol", usuario.getRol().name());
        claims.put("email", usuario.getEmail());

        String token = jwtUtil.generateToken(userDetails.getUsername(), claims);

        log.info("Login exitoso para usuario: {}", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .expiration(jwtExpiration)
                .build();
    }

    /**
     * Valida un token JWT
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Extrae el username de un token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
}
