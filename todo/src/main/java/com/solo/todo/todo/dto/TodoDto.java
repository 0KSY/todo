package com.solo.todo.todo.dto;

import com.solo.todo.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class TodoDto {

    @Getter
    @Setter
    public static class Post{

        @NotNull
        private LocalDate date;
        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @Positive
        private long memberId;
    }

    @Getter
    @Setter
    public static class Patch{
        private long todoId;
        private String title;
        private String description;
        private Todo.IsCompleted isCompleted;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long todoId;
        private LocalDate date;
        private String title;
        private String description;
        private Todo.IsCompleted isCompleted;
    }
}
