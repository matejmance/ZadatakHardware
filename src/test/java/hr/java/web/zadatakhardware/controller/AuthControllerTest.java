package hr.java.web.zadatakhardware.controller;

import hr.java.web.zadatakhardware.domain.RefreshToken;
import hr.java.web.zadatakhardware.domain.UserInfo;
import hr.java.web.zadatakhardware.dto.AuthRequestDTO;
import hr.java.web.zadatakhardware.dto.JwtResponseDTO;
import hr.java.web.zadatakhardware.dto.RefreshTokenRequestDTO;
import hr.java.web.zadatakhardware.service.JwtService;
import hr.java.web.zadatakhardware.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testAuthenticateAndGetToken_Success() {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("test-refresh-token");

        when(refreshTokenService.createRefreshToken("admin"))
                .thenReturn(refreshToken);

        when(jwtService.generateToken("admin"))
                .thenReturn("test-access-token");


        JwtResponseDTO response =
                authController.authenticateAndGetToken(request);


        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getToken());


        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin"));

        verify(refreshTokenService, times(1))
                .createRefreshToken("admin");

        verify(jwtService, times(1))
                .generateToken("admin");

        verifyNoMoreInteractions(
                authenticationManager, refreshTokenService, jwtService);
    }

    @Test
    void testAuthenticateAndGetToken() {

        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername("pogresan");
        request.setPassword("pogresna-lozinka");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));


        assertThrows(
                BadCredentialsException.class,
                () -> authController.authenticateAndGetToken(request)
        );


        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(
                        "pogresan", "pogresna-lozinka")
        );


        verifyNoInteractions(jwtService, refreshTokenService);

        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    void testRefreshToken_Success() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setToken("test-refresh-token");

        UserInfo user = new UserInfo();
        user.setUsername("admin");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("test-refresh-token");
        refreshToken.setUserInfo(user);

        when(refreshTokenService.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        when(refreshTokenService.verifyExpiration(refreshToken))
                .thenReturn(refreshToken);

        when(jwtService.generateToken("admin"))
                .thenReturn("new-test-access-token");


        JwtResponseDTO response = authController.refreshToken(request);


        assertNotNull(response);
        assertEquals("new-test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getToken());

        verify(refreshTokenService, times(1))
                .findByToken("test-refresh-token");

        verify(refreshTokenService, times(1))
                .verifyExpiration(refreshToken);

        verify(jwtService, times(1))
                .generateToken("admin");

        verifyNoInteractions(authenticationManager);

        verifyNoMoreInteractions(refreshTokenService, jwtService);
    }

    @Test
    void testRefreshToken_Failure() {

        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setToken("neispravan-refresh-token");

        when(refreshTokenService.findByToken("neispravan-refresh-token"))
                .thenReturn(Optional.empty());


        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authController.refreshToken(request)
        );

        assertEquals("Neispravan refresh token", exception.getMessage());

        verify(refreshTokenService, times(1))
                .findByToken("neispravan-refresh-token");

        verify(refreshTokenService, never())
                .verifyExpiration(any(RefreshToken.class));

        verifyNoInteractions(authenticationManager, jwtService);

        verifyNoMoreInteractions(refreshTokenService);
    }
}