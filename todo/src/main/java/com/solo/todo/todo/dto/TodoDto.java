package com.solo.todo.todo.dto;

import com.solo.todo.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class TodoDto {

    @Getter
    @Setter
    public static class Post{
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
        private String title;
        private String description;
        private Todo.IsCompleted isCompleted;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
