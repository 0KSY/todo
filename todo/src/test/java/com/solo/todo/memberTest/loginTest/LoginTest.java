package com.solo.todo.memberTest.loginTest;

import com.google.gson.Gson;
import com.solo.todo.auth.loginDto.LoginDto;
import com.solo.todo.auth.utils.CustomAuthorityUtils;
import com.solo.todo.member.entity.Member;
import com.solo.todo.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static com.solo.todo.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.todo.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;
    @MockBean
    private MemberRepository memberRepository;

    @Test
    void memberLoginTest() throws Exception{

        Member member = new Member();
        member.setMemberId(1L);
        member.setEmail("hgd@naver.com");
        member.setPassword(passwordEncoder.encode("1234"));
        member.setNickname("홍길동");
        member.setRoles(customAuthorityUtils.createRoles(member.getEmail()));

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("hgd@naver.com");
        loginDto.setPassword("1234");

        String requestBody = gson.toJson(loginDto);

        given(memberRepository.findByEmail(Mockito.any(String.class))).willReturn(Optional.of(member));

        ResultActions resultActions = mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("memberId").value(member.getMemberId()))
                .andDo(document(
                        "auth-login",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                )
                        ),
                        responseHeaders(
                                List.of(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token"),
                                        headerWithName("Refresh").description("Refresh Token")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                )
                        )
                ));


    }


}
