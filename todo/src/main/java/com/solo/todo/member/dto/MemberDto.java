package com.solo.todo.member.dto;

import com.solo.todo.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class MemberDto {

    @Getter
    @Setter
    public static class Post{

        @NotBlank
        private String email;
        @NotBlank
        private String nickname;

    }

    @Getter
    @Setter
    public static class Patch{

        private long memberId;
        private String nickname;
        private Member.MemberStatus memberStatus;

    }

    @Getter
    @Setter
    @Builder
    public static class Response{

        private long memberId;
        private String email;
        private String nickname;
        private Member.MemberStatus memberStatus;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

    }



}
