package com.akshayan.authservice.service;

import com.akshayan.authservice.dto.AuthenticationResponse;
import com.akshayan.authservice.dto.LoginRequest;
import com.akshayan.authservice.dto.RefreshTokenRequest;
import com.akshayan.authservice.dto.RegisterRequestDto;
import com.akshayan.authservice.event.ActivateAccountEvent;
import com.akshayan.authservice.exception.ForumException;
import com.akshayan.authservice.model.ForumUser;
import com.akshayan.authservice.model.VerificationToken;
import com.akshayan.authservice.repository.ForumUserRepository;
import com.akshayan.authservice.repository.VerificationTokenRepository;
import com.akshayan.authservice.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final ForumUserRepository forumUserRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final KafkaTemplate<String, ActivateAccountEvent> kafkaTemplate;

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {

        ForumUser oldUser = forumUserRepository.findByEmail(registerRequestDto.getEmail());
        Optional<ForumUser> oldUser1 = forumUserRepository.findByUsername(registerRequestDto.getUsername());
        if (oldUser != null) {
            throw new ForumException("Email already exists");
        }
        if(oldUser1.isPresent()){
            throw new ForumException("Username already exists");
        }

        ForumUser forumUser = new ForumUser();


        forumUser.setUsername(registerRequestDto.getUsername());
        forumUser.setEmail(registerRequestDto.getEmail());
        forumUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        forumUser.setCreated(Instant.now());
        forumUser.setEnabled(false);

        forumUserRepository.save(forumUser);

        String token = generateVerificationToken(forumUser);

        kafkaTemplate.send("activationEmail", new ActivateAccountEvent(forumUser.getEmail(), token));

    }

    private String generateVerificationToken(ForumUser forumUser) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setForumUser(forumUser);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken= verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow((() -> new ForumException("Invalid Token")));
        fetchUserAndEnable(verificationToken.get());
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getForumUser().getUsername();
        Optional<ForumUser> forumUser = forumUserRepository.findByUsername(username);
        forumUser.orElseThrow((() -> new ForumException("User not found with name: " + username)));
        forumUser.get().setEnabled(true);
        forumUserRepository.save(forumUser.get());
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
//                .refreshToken("")
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }


    public boolean isLoggedIn() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

//    }

    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        ForumUser forumUser= forumUserRepository.findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
        return forumUser.getUserId();
    }
    @Transactional(readOnly = true)
    public Long getUserIdByName(String username) {
        ForumUser forumUser= forumUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + username));
        return forumUser.getUserId();
    }
    @Transactional(readOnly = true)
    public String getUserNameByUserId(Long userId) {
        ForumUser forumUser= forumUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + userId));
        return forumUser.getUsername();
    }


}
