package com.jwbaek.backendProj.Users.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwbaek.backendProj.Users.entity.UserLoginDto;
import com.jwbaek.backendProj.Users.service.LoginService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final  LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
 
    }

    /** 로그인 요청 URL */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto dto) {
        try {
            Map<String, Object> responseData = loginService.authAndGetToken(dto.getUserId(), dto.getPassword());
            return ResponseEntity.ok(responseData);            // 서비스에서 받은 결과 반환
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 일치하지 않습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) 
                    .body(Map.of("message", e.getMessage()));
        }
    }
     
    // 클라이언트가 JWT를 삭제하도록 유도하는 역할
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}