package org.example.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с ошибкой")
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибке",
            example = "Пользователь с id=5 не найден")
    private String message;

    @Schema(description = "Ошибки валидации по полям (опционально)",
            example = "{\"email\": \"must be a well-formed email address\"}")
    private Map<String, String> errors;


    public ErrorResponse(String message) {
        this.message = message;
    }
}
