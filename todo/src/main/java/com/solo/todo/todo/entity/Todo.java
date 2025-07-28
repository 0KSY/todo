package com.solo.todo.todo.entity;

import com.solo.todo.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IsCompleted isCompleted = IsCompleted.NONE;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    public enum IsCompleted{
        DONE("완료"),
        NONE("미완료");

        @Getter
        private String status;

        IsCompleted(String status) {
            this.status = status;
        }
    }

}
