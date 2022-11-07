package com.akshayan.authservice.controller;

import com.akshayan.authservice.dto.AuthenticationResponse;
import com.akshayan.authservice.dto.LoginRequest;
import com.akshayan.authservice.dto.RefreshTokenRequest;
import com.akshayan.authservice.dto.RegisterRequestDto;
import com.akshayan.authservice.service.AuthService;
import com.akshayan.authservice.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {


    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return new ResponseEntity<>("Registration Successful", OK);
    }
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Verified Successfully", OK);
    }
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
    }
    @GetMapping("/current-user")
    public ResponseEntity<Long> getCurrentUser(){
        return ResponseEntity.status(OK).body(authService.getCurrentUserId());
    }
    @GetMapping("/user-by-username/{userName}")
    public ResponseEntity<Long> getUserIdByUserName(@PathVariable String userName){
        return ResponseEntity.status(OK).body(authService.getUserIdByName(userName));
    }
    @GetMapping("/username-by-userid/{userId}")
    public ResponseEntity<String> getUserNameByUserId(@PathVariable Long userId){
        return ResponseEntity.status(OK).body(authService.getUserNameByUserId(userId));
    }
    @GetMapping("/isLoggedIn")
    public ResponseEntity<Boolean> isLogged(){
        return ResponseEntity.status(OK).body(authService.isLoggedIn());
    }


}
