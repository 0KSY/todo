package com.solo.todo.auth.handler;

import com.solo.todo.auth.utils.ErrorResponder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if(exception instanceof BadCredentialsException){
            ErrorResponder.sendPasswordErrorResponse(response, HttpStatus.UNAUTHORIZED);
        }
        else{
            ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
        }

    }
}
