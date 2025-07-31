package com.solo.todo.todo.service;


import com.solo.todo.exception.BusinessLogicException;
import com.solo.todo.exception.ExceptionCode;
import com.solo.todo.member.service.MemberService;
import com.solo.todo.todo.entity.Todo;
import com.solo.todo.todo.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberService memberService;

    public TodoService(TodoRepository todoRepository, MemberService memberService) {
        this.todoRepository = todoRepository;
        this.memberService = memberService;
    }

    public Todo findVerifiedTodo(long todoId){
        Optional<Todo> optionalTodo = todoRepository.findById(todoId);

        Todo findTodo = optionalTodo
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TODO_NOT_FOUND));

        return findTodo;

    }

    public Todo createTodo(Todo todo, String accessToken){

        memberService.checkMemberId(todo.getMember().getMemberId(), accessToken);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Todo todo, String accessToken){

        memberService.checkMemberId(todo.getMember().getMemberId(), accessToken);

        Todo findTodo = findVerifiedTodo(todo.getTodoId());

        Optional.ofNullable(todo.getTitle())
                .ifPresent(title -> findTodo.setTitle(title));

        Optional.ofNullable(todo.getDescription())
                .ifPresent(description -> findTodo.setDescription(description));

        if(todo.getIsCompleted() != findTodo.getIsCompleted()){
            findTodo.setIsCompleted(todo.getIsCompleted());
        }

        return todoRepository.save(findTodo);

    }

    public Todo findTodo(long todoId, String accessToken){

        Todo findTodo = findVerifiedTodo(todoId);

        memberService.checkMemberId(findTodo.getMember().getMemberId(), accessToken);

        return findTodo;
    }

    public Page<Todo> findTodos(int page, int size, String accessToken){

        Page<Todo> pageTodos = todoRepository.findAll(
                PageRequest.of(page, size, Sort.by("todoId").descending()));

        if(pageTodos.isEmpty()){
            return pageTodos;
        }

        Todo todo = pageTodos.getContent().get(0);

        memberService.checkMemberId(todo.getMember().getMemberId(), accessToken);

        return pageTodos;

    }

    public void deleteTodo(long todoId, String accessToken){
        Todo findTodo = findVerifiedTodo(todoId);

        memberService.checkMemberId(findTodo.getMember().getMemberId(), accessToken);

        todoRepository.delete(findTodo);
    }


}
