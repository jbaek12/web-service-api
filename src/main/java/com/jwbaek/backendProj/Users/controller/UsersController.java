package com.jwbaek.backendProj.Users.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jwbaek.backendProj.Users.entity.UserSignupDto;
import com.jwbaek.backendProj.Users.service.UsersService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService userService){
        this.usersService = userService;
    }

    // ID 중복 체크
    @GetMapping("/chkIdDup")
    public Boolean chkIdDup(@RequestParam String userId) {
        return usersService.chkIdDup(userId);
    }
    
    // 회원가입 정보 저장
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid@RequestBody UserSignupDto dto) {

        usersService.signup(dto);

        return ResponseEntity.ok().build();
    }

    // 유효성 검사 실패 시 예외 처리 핸들러
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    
    
}
