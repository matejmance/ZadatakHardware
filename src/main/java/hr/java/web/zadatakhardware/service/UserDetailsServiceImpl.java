package hr.java.web.zadatakhardware.service;

import hr.java.web.zadatakhardware.domain.CustomUserDetails;
import hr.java.web.zadatakhardware.domain.UserInfo;
import hr.java.web.zadatakhardware.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        UserInfo userInfo = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Korisnik nije pronaden"));

        return new CustomUserDetails(userInfo);
    }
}