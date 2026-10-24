package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "Объект пользователя для частичного обновления")
public class UserPatchDto {

    @Schema(description = "Имя пользователя",
            example = "Иван",
            minLength = 2, maxLength = 20)
    @Size(min = 2, max = 20, message = "Имя от 2 до 20 символов")
    private String name;


    @Schema(description = "Возраст",
            example = "25",
            minimum = "0", maximum = "100")
    @Min(value = 0, message = "Возраст не может быть отрицательным!")
    @Max(value = 100, message = "Возраст не может быть больше 100")
    private Integer age;

    @Schema(description = "Email",
            example = "ivan@mail.ru")
    @Email(message = "Некорректный email")
    private String email;

}
