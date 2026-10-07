package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.domain.RefreshToken;
import hr.java.web.zadatakhardware.dto.AuthRequestDTO;
import hr.java.web.zadatakhardware.dto.JwtResponseDTO;
import hr.java.web.zadatakhardware.dto.RefreshTokenRequestDTO;
import hr.java.web.zadatakhardware.service.JwtService;
import hr.java.web.zadatakhardware.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public JwtResponseDTO authenticateAndGetToken(
            @Valid @RequestBody AuthRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()));

        String username = authentication.getName();

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(username);

        return new JwtResponseDTO(
                jwtService.generateToken(username),
                refreshToken.getToken());
    }

    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO request) {

        RefreshToken token =
                refreshTokenService.findByToken(request.getToken())
                        .map(refreshTokenService::verifyExpiration)
                        .orElseThrow(() ->
                                new BadCredentialsException(
                                        "Neispravan refresh token"));

        return new JwtResponseDTO(
                jwtService.generateToken(
                        token.getUserInfo().getUsername()),
                token.getToken());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationError(
            AuthenticationException exception) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error",
                        "Neispravni podaci za prijavu ili refresh token"));
    }
}