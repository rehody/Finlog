package org.example.finlog.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.finlog.DTO.UserRequest;
import org.example.finlog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<Void> update(
            @Valid @RequestBody UserRequest request,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        log.debug("Attempt to update user '{}'", username);
        userService.update(username, request);
        log.debug("User updated: {}", username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Principal principal
    ) throws AccessDeniedException {
        String username = principal.getName();
        log.debug("Attempt to delete user '{}'", username);
        userService.delete(username, id);
        log.debug("User deleted:: {}", username);
        return ResponseEntity.noContent().build();
    }

}
