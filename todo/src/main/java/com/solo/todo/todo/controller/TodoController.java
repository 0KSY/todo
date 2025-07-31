package com.solo.todo.todo.controller;

import com.solo.todo.dto.MultiResponseDto;
import com.solo.todo.dto.SingleResponseDto;
import com.solo.todo.todo.dto.TodoDto;
import com.solo.todo.todo.entity.Todo;
import com.solo.todo.todo.mapper.TodoMapper;
import com.solo.todo.todo.service.TodoService;
import com.solo.todo.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/todos")
@Validated
public class TodoController {

    private final TodoService todoService;
    private final TodoMapper mapper;
    private final static String TODO_DEFAULT_URL = "/todos";

    public TodoController(TodoService todoService, TodoMapper mapper) {
        this.todoService = todoService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postTodo(@RequestBody @Valid TodoDto.Post todoPostDto,
                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        Todo todo = todoService.createTodo(mapper.todoPostDtoToTodo(todoPostDto), accessToken);

        URI location = UriCreator.createUri(TODO_DEFAULT_URL, todo.getTodoId());

        return ResponseEntity.created(location).build();

    }

    @PatchMapping("/{todo-id}")
    public ResponseEntity patchTodo(@RequestBody @Valid TodoDto.Patch todoPatchDto,
                                    @PathVariable("todo-id") @Positive long todoId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        todoPatchDto.setTodoId(todoId);

        Todo todo = todoService.updateTodo(mapper.todoPatchDtoToTodo(todoPatchDto), accessToken);

        return new ResponseEntity(new SingleResponseDto<>(mapper.todoToTodoResponseDto(todo)), HttpStatus.OK);

    }

    @GetMapping("/{todo-id}")
    public ResponseEntity getTodo(@PathVariable("todo-id") @Positive long todoId,
                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        Todo todo = todoService.findTodo(todoId, accessToken);

        return new ResponseEntity(new SingleResponseDto<>(mapper.todoToTodoResponseDto(todo)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getTodos(@RequestParam @Positive int page,
                                   @RequestParam @Positive int size,
                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        Page<Todo> pageTodos = todoService.findTodos(page-1, size, accessToken);
        List<Todo> todos = pageTodos.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.todosToTodoResponseDtos(todos), pageTodos), HttpStatus.OK);
    }

    @DeleteMapping("/{todo-id}")
    public ResponseEntity deleteTodo(@PathVariable("todo-id") @Positive long todoId,
                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        todoService.deleteTodo(todoId, accessToken);

        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
}
