package com.solo.todo.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_MATCHED(403, "MemberId not matched"),
    MEMBER_PASSWORD_NOT_MATCHED(403, "Member password not matched"),
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    MEMBER_NICKNAME_EXISTS(409, "Member nickname exists"),
    MEMBER_SERVER_USER(409, "This email is already registered using email and password"),
    MEMBER_GOOGLE_OAUTH2_USER(409, "This email is already registered using Google"),
    TODO_NOT_FOUND(404, "Todo not found");

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
