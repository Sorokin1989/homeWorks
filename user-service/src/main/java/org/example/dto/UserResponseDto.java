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

    @Schema(description = "Уникальный идентификатор", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;

    @Schema(description = "Возраст", example = "25")
    private Integer age;

    @Schema(description = "Email", example = "ivan@mail.ru")
    private String email;

    @Schema(description = "Дата и время создания",
            example = "2024-05-01T12:34:56")
    private LocalDateTime createdAt;
}