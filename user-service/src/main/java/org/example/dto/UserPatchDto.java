package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект пользователя для частичного обновления")
public class UserPatchDto {

    @Size(min = 2, max = 20, message = "Имя от 2 до 20 символов")
    private String name;


    @Min(value = 0, message = "Возраст не может быть отрицательным!")
    @Max(value = 100, message = "Возраст не может быть больше 100")
    private Integer age;

    @Email(message = "Некорректный email")
    private String email;

}
