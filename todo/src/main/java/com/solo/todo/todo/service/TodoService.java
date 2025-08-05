package com.solo.todo.todo.service;


import com.solo.todo.auth.userDetailsService.CustomUserDetails;
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

import java.time.LocalDate;
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

    public Todo createTodo(Todo todo, CustomUserDetails customUserDetails){

        memberService.checkMemberId(todo.getMember().getMemberId(), customUserDetails);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(Todo todo, CustomUserDetails customUserDetails){

        Todo findTodo = findVerifiedTodo(todo.getTodoId());

        memberService.checkMemberId(findTodo.getMember().getMemberId(), customUserDetails);

        Optional.ofNullable(todo.getTitle())
                .ifPresent(title -> findTodo.setTitle(title));

        Optional.ofNullable(todo.getDescription())
                .ifPresent(description -> findTodo.setDescription(description));

        if(todo.getIsCompleted() != findTodo.getIsCompleted()){
            findTodo.setIsCompleted(todo.getIsCompleted());
        }

        return todoRepository.save(findTodo);

    }

    public Todo findTodo(long todoId, CustomUserDetails customUserDetails){

        Todo findTodo = findVerifiedTodo(todoId);

        memberService.checkMemberId(findTodo.getMember().getMemberId(), customUserDetails);

        return findTodo;
    }

    public Page<Todo> findTodos(int page, int size, LocalDate date, CustomUserDetails customUserDetails){

        long memberId = customUserDetails.getMemberId();

        return todoRepository.findAllByMember_MemberIdAndDate(
                memberId, date, PageRequest.of(page, size, Sort.by("todoId")));

    }

    public void deleteTodo(long todoId, CustomUserDetails customUserDetails){
        Todo findTodo = findVerifiedTodo(todoId);

        memberService.checkMemberId(findTodo.getMember().getMemberId(), customUserDetails);

        todoRepository.delete(findTodo);

    }


}
