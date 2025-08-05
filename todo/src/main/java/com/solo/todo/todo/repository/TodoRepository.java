package com.solo.todo.todo.repository;

import com.solo.todo.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findAllByMember_MemberIdAndDate(long memberId, LocalDate date, Pageable pageable);
}
