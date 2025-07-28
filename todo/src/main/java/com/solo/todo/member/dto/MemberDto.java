package com.solo.todo.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberDto {

    @Getter
    @Setter
    public static class Post{

        private String email;
        private String nickname;

    }

    @Getter
    @Setter
    public static class Patch{

        private long memberId;
        private String nickname;

    }

    @Getter
    @Setter
    @Builder
    public static class Response{

        private long memberId;
        private String email;
        private String nickname;

    }



}
