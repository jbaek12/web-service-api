package com.jwbaek.backendProj.Users.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 1, max = 30, message = "아이디는 1자 이상 30자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(max = 10, message = "이름은 최대 10자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 30, message = "이메일은 최대 30자 이하여야 합니다.")
    private String email;

    @Size(min = 10, max = 11, message = "전화번호는 최소 10자에서 최대 11자까지 입력할 수 있습니다.")
    private String phoneNum;

    @Size(max = 5, message = "우편번호는 최대 5자 이하여야 합니다.")
    private String areaCode;

    @NotBlank(message = "기본 주소는 필수 입력 값입니다.")
    @Size(max = 100, message = "기본 주소는 최대 100자 이하여야 합니다.")
    private String addr1;

    @Size(max = 100, message = "상세 주소는 최대 100자 이하여야 합니다.")
    private String addr2;
    
}
