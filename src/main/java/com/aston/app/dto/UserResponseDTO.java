package com.aston.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Объект ответа пользователя")
public class UserResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Sarah Jenkins")
    private String name;
    @Schema(description = "Email пользователя", example = "sarahjenkins@gmail.com")
    private String email;
    @Schema(description = "Возраст пользователя", example = "25")
    private Integer age;
    @Schema(description = "Время создания пользователя", example = "2024-07-27T10:00:00")
    private LocalDateTime createdAt;
}
