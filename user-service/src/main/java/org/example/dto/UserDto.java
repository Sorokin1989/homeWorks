package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

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
    public UserDto(String name,Integer age,String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
}
