package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.RefreshToken;
import hr.java.web.zadatakhardware.domain.UserInfo;
import hr.java.web.zadatakhardware.repository.RefreshTokenRepository;
import hr.java.web.zadatakhardware.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTokenValiditySeconds;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            @Value("${jwt.refresh-token-validity-seconds}") long validitySeconds) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenValiditySeconds = validitySeconds;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        UserInfo userInfo = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadCredentialsException("Korisnik nije pronaden"));

        RefreshToken token = new RefreshToken();
        token.setUserInfo(userInfo);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plusSeconds(refreshTokenValiditySeconds));

        return refreshTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (!token.getExpiryDate().isAfter(Instant.now())) {
            refreshTokenRepository.delete(token);

            throw new BadCredentialsException(
                    "Refresh token je istekao. Prijavi se ponovno.");
        }

        return token;
    }
}