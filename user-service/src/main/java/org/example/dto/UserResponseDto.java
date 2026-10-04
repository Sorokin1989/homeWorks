package org.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String name;

    private Integer age;

    private String email;

    private LocalDateTime createdAt;
}
