package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.user.UserRequestDTO;
import com.group1.shoprider.dtos.user.UserResponseDTO;
import com.group1.shoprider.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("")
    @Secured({"ADMIN", "SUPER-ADMIN"})
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PutMapping("")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Valid
            @RequestParam(required = false) Long userID,
            @RequestBody UserRequestDTO userData,
            HttpServletRequest request) {
        UserResponseDTO response = userService.updateUser(userData, request, userID);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userID}")
    @Secured("SUPER-ADMIN")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userID) {
        userService.deleteUser(userID);
        return ResponseEntity.noContent().build();
    }
}
