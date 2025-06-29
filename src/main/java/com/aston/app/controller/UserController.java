package com.aston.app.controller;

import com.aston.app.dto.UserDTO;
import com.aston.app.dto.UserResponseDTO;
import com.aston.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping
    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей с HATEOAS ссылками.")
    @ApiResponse(responseCode = "200", description = "Успешный запрос",
            content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UserResponseDTO.class)))
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAllUsers() {
        List<EntityModel<UserResponseDTO>> userModels = userService.getAllUsers().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"),
                        linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete")))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        CollectionModel<EntityModel<UserResponseDTO>> collectionModel = CollectionModel.of(userModels, selfLink);

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя",
            description = "Создает нового пользователя и возвращает его сгенерированный ID.")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content)
    public ResponseEntity<EntityModel<UserResponseDTO>> createUser(@RequestBody UserDTO userDTO) {
        UserResponseDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(createdUser.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(createdUser.getId())).withRel("delete")),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному ID.")
    @ApiResponse(responseCode = "200", description = "Пользователь найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return new ResponseEntity<>(EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete")),
                HttpStatus.OK);
    }

    @GetMapping("/email")
    @Operation(summary = "Получить пользователя по email",
            description = "Возвращает пользователя по указанному email.")
    @ApiResponse(responseCode = "200", description = "Пользователь найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserByEmail(@Parameter(description = "Email пользователя") @RequestParam String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return new ResponseEntity<>(EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserByEmail(email)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete")),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по указанному ID.")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(@Parameter(description = "ID пользователя") @PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userDTO);
        return new ResponseEntity<>(EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withSelfRel(),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete")),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному ID.")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    public ResponseEntity<HttpStatus> deleteUser(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}