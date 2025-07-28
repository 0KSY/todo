package com.solo.todo.todo.service;


import com.solo.todo.exception.BusinessLogicException;
import com.solo.todo.exception.ExceptionCode;
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

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo findVerifiedTodo(long todoId){
        Optional<Todo> optionalTodo = todoRepository.findById(todoId);

        Todo findTodo = optionalTodo
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TODO_NOT_FOUND));

        return findTodo;

    }

    public Todo createTodo(Todo todo){
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Todo todo){

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

    public Todo findTodo(long todoId){
        return findVerifiedTodo(todoId);
    }

    public Page<Todo> findTodos(int page, int size){
        return todoRepository.findAll(
                PageRequest.of(page, size, Sort.by("todoId").descending())
        );
    }

    public void deleteTodo(long todoId){
        Todo findTodo = findVerifiedTodo(todoId);

        todoRepository.delete(findTodo);
    }


}
