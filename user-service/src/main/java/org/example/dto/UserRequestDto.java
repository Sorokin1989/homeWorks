package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект пользователя для запроса")
public class UserRequestDto {


    @NotBlank(message = "Имя не может быть пустым!")
    @Size(min = 2, max = 20, message = "Имя от 2 до 20 символов")
    private String name;


    @NotNull(message = "Поле не может быть пустым!")
    @Min(value = 0, message = "Возраст не может быть отрицательным!")
    @Max(value = 100, message = "Возраст не может быть больше 100")
    private Integer age;

    @NotBlank(message = "Email не может быть пустым!")
    @Email(message = "Некорректный email")
    private String email;




}

