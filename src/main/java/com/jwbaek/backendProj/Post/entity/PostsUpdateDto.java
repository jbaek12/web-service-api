package com.jwbaek.backendProj.Post.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PostsUpdateDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 최대 20자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private LocalDateTime updateDate;

    public PostsUpdateDto(String title, String content, LocalDateTime upDateTime) {
        this.title = title;
        this.content = content;
        this.updateDate = upDateTime;
    }
    
}
