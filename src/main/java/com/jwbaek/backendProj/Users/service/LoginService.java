package com.jwbaek.backendProj.Users.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.jwbaek.backendProj.jwt.JwtUtil;
import com.jwbaek.backendProj.Users.entity.Users;
import com.jwbaek.backendProj.Users.entity.UsersRepository;

import java.util.Map; 

@Service
public class LoginService implements UserDetailsService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    public LoginService(@Lazy AuthenticationManager authenticationManager,UsersRepository usersRepository, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
    }
    
    /** 토큰생성용 : 사용자 존재 확인 */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
   
        Optional<Users> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) { //사용자를 못 찾았을 경우
            throw new UsernameNotFoundException("NOT FOUND USER: " + userId);
        }

        Users user = userOptional.get();

        // 조회된 Users 엔티티 정보를 Spring Security의 UserDetails 객체로 변환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId()) 
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    
    }

    /** 토큰 생성 및 사용자 정보 리턴 */
    @Transactional 
    public Map<String, Object> authAndGetToken(String userId, String password) {

        // AuthenticationManager를 통해 사용자 인증 시도
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userId, password)
        );

        UserDetails userDetails = loadUserByUsername(userId);

        // JWT 토큰 생성
        String jwtToken = jwtUtil.generateToken(userDetails);

        Users user = usersRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("사용자 정보가 존재하지 않습니다.")); 

        return Map.of(
            "message", "로그인 성공",
            "token", jwtToken,
            "user", Map.of(
                                "userId", user.getUserId(), 
                                "name", user.getName(),
                                "role", user.getRole().name()) 
        );
    }

    
}
