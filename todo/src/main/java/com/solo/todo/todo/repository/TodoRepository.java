package com.solo.todo.todo.repository;

import com.solo.todo.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findAllByMember_MemberId(long memberId, Pageable pageable);
}
