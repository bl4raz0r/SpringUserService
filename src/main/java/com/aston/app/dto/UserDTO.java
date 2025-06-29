package com.aston.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Объект запроса для создания/обновления пользователя")
public class UserDTO {
    @Schema(description = "Имя пользователя", example = "Sarah Jenkins")
    private String name;
    @Schema(description = "Email пользователя", example = "sarahjenkins@gmail.com")
    private String email;
    @Schema(description = "Возраст пользователя", example = "25")
    private Integer age;
}