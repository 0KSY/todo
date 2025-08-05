package com.solo.todo.todo.mapper;

import com.solo.todo.member.entity.Member;
import com.solo.todo.todo.dto.TodoDto;
import com.solo.todo.todo.entity.Todo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    default Todo todoPostDtoToTodo(TodoDto.Post todoPostDto){

        Member member = new Member();
        member.setMemberId(todoPostDto.getMemberId());

        Todo todo = new Todo();
        todo.setDate(todoPostDto.getDate());
        todo.setTitle(todoPostDto.getTitle());
        todo.setDescription(todoPostDto.getDescription());
        todo.setMember(member);

        return todo;
    }

    Todo todoPatchDtoToTodo(TodoDto.Patch todoPatchDto);

    TodoDto.Response todoToTodoResponseDto(Todo todo);

    List<TodoDto.Response> todosToTodoResponseDtos(List<Todo> todos);
}
