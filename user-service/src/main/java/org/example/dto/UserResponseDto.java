package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Объект пользователя для ответа")
public class UserResponseDto {

    private Long id;

    private String name;

    private Integer age;

    private String email;

    private LocalDateTime createdAt;
}
