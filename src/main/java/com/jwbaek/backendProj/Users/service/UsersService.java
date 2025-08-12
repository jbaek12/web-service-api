package com.jwbaek.backendProj.Users.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.jwbaek.backendProj.Users.entity.Role;
import com.jwbaek.backendProj.Users.entity.UserSignupDto;
import com.jwbaek.backendProj.Users.entity.Users;
import com.jwbaek.backendProj.Users.entity.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder; 

    public UsersService(UsersRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 ID 중복체크 
    public boolean chkIdDup(String userId){
        return userRepository.existsById(userId);
    }

    // 회원가입 정보 저장
    public void signup(UserSignupDto dto){

        // 사용자 ID 중복여부 더블 체크
        if(chkIdDup(dto.getUserId())) {
            throw new RuntimeException("이미 사용중인 아이디입니다.");
        }

       String encodedPass = passwordEncoder.encode(dto.getPassword());

        Users newUser = Users.builder()
                .userId(dto.getUserId())
                .password(encodedPass) 
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNum(dto.getPhoneNum())
                .areaCode(dto.getAreaCode())
                .addr1(dto.getAddr1())
                .addr2(dto.getAddr2())
                .role(Role.USER) 
                .build();

        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            System.err.println("회원가입 실패: " + e.getMessage());
            throw new RuntimeException("회원가입 처리 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

}
