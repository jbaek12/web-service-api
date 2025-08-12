package com.jwbaek.backendProj.Post.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentsUpdateDto {
    
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(max = 100, message = "내용은 최대 500자 이하여야 합니다.")
    private String content;
    
}
