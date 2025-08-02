package com.solo.todo.todoTest.controllerTest;

import com.google.gson.Gson;
import com.solo.todo.auth.userDetailsService.CustomUserDetails;
import com.solo.todo.auth.utils.CustomAuthorityUtils;
import com.solo.todo.todo.dto.TodoDto;
import com.solo.todo.todo.entity.Todo;
import com.solo.todo.todo.mapper.TodoMapper;
import com.solo.todo.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.solo.todo.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.todo.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;

    @MockBean
    private TodoService todoService;
    @MockBean
    private TodoMapper mapper;

    private Todo todo;
    private Todo todo2;
    private TodoDto.Post todoPostDto;
    private TodoDto.Patch todoPatchDto;
    private TodoDto.Response response;
    private TodoDto.Response response2;

    @BeforeEach
    void init(){

        todo = new Todo();
        todo.setTodoId(1L);
        todo.setTitle("제목");
        todo.setDescription("내용");

        todo2 = new Todo();
        todo2.setTodoId(2L);
        todo2.setTitle("제목2");
        todo2.setDescription("내용");

        response = TodoDto.Response.builder()
                .todoId(1L)
                .title("제목")
                .description("내용")
                .isCompleted(Todo.IsCompleted.NONE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        response2 = TodoDto.Response.builder()
                .todoId(2L)
                .title("제목2")
                .description("내용2")
                .isCompleted(Todo.IsCompleted.NONE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(1L);
        customUserDetails.setEmail("hgd@naver.com");
        customUserDetails.setRoles(customAuthorityUtils.createRoles("hgd@naver.com"));
        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(customUserDetails.getRoles()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);


    }

    @Test
    void postTodoTest() throws Exception{

        todoPostDto = new TodoDto.Post();
        todoPostDto.setTitle("제목");
        todoPostDto.setDescription("내용");
        todoPostDto.setMemberId(1L);

        String requestBody = gson.toJson(todoPostDto);

        given(mapper.todoPostDtoToTodo(Mockito.any(TodoDto.Post.class))).willReturn(new Todo());
        given(todoService.createTodo(Mockito.any(Todo.class), Mockito.any(CustomUserDetails.class))).willReturn(todo);

        ResultActions resultActions = mockMvc.perform(
                post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/todos"))))
                .andDo(document(
                        "post-todo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )
                ));

    }

    @Test
    void patchTodoTest() throws Exception{

        todoPatchDto = new TodoDto.Patch();
        todoPatchDto.setTodoId(1L);
        todoPatchDto.setTitle("제목");
        todoPatchDto.setDescription("내용");
        todoPatchDto.setIsCompleted(Todo.IsCompleted.NONE);

        String requestBody = gson.toJson(todoPatchDto);

        given(mapper.todoPatchDtoToTodo(Mockito.any(TodoDto.Patch.class))).willReturn(new Todo());
        given(todoService.updateTodo(Mockito.any(Todo.class), Mockito.any(CustomUserDetails.class))).willReturn(new Todo());
        given(mapper.todoToTodoResponseDto(Mockito.any(Todo.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/todos/{todo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoId").value(response.getTodoId()))
                .andDo(document(
                        "patch-todo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todo-id").description("투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("todoId").type(JsonFieldType.NUMBER).description("투두 식별자").ignored(),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("내용").optional(),
                                        fieldWithPath("isCompleted").type(JsonFieldType.STRING)
                                                .description("완료 여부 : NONE / DONE").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.todoId").type(JsonFieldType.NUMBER).description("투두 식별자"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.isCompleted").type(JsonFieldType.STRING).
                                                description("완료 여부 : NONE / DONE"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                                )
                        )
                ));
    }

    @Test
    void getTodoTest() throws Exception{

        given(todoService.findTodo(Mockito.any(long.class), Mockito.any(CustomUserDetails.class))).willReturn(new Todo());
        given(mapper.todoToTodoResponseDto(Mockito.any(Todo.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/todos/{todo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoId").value(response.getTodoId()))
                .andDo(document(
                        "get-todo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todo-id").description("투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.todoId").type(JsonFieldType.NUMBER).description("투두 식별자"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data.isCompleted").type(JsonFieldType.STRING)
                                                .description("완료 여부 : NONE / DONE"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                                )
                        )
                ));
    }

    @Test
    void getTodosTest() throws Exception{

        Page<Todo> pageTodos = new PageImpl<>(List.of(todo, todo2),
                PageRequest.of(0, 5, Sort.by("todoId")),2);

        given(todoService.findTodos(Mockito.any(int.class), Mockito.any(int.class),Mockito.any(CustomUserDetails.class)))
                .willReturn(pageTodos);
        given(mapper.todosToTodoResponseDtos(Mockito.any(List.class))).willReturn(List.of(response, response2));

        ResultActions resultActions = mockMvc.perform(
                get("/todos")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].todoId").value(response.getTodoId()))
                .andExpect(jsonPath("$.data[1].todoId").value(response2.getTodoId()))
                .andDo(document(
                        "get-todos",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                )
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
                                        fieldWithPath("data[].todoId").type(JsonFieldType.NUMBER).description("투두 식별자"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("data[].isCompleted").type(JsonFieldType.STRING)
                                                .description("완료 상태 : NONE / DONE"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER)
                                                .description("총 데이터 개수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER)
                                                .description("총 페이지 개수")
                                )
                        )
                ));

    }

    @Test
    void deleteTodoTest() throws Exception{

        doNothing().when(todoService).deleteTodo(Mockito.any(long.class), Mockito.any(CustomUserDetails.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/todos/{todo-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Access Token")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-todo",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("todo-id").description("투두 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));

    }



}
