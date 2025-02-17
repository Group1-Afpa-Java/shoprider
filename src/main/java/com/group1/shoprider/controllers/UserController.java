package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.role.RoleRequestDto;
import com.group1.shoprider.dtos.user.UserDetailsDTO;
import com.group1.shoprider.dtos.user.UserRequestDTO;
import com.group1.shoprider.dtos.user.UserResponseDTO;
import com.group1.shoprider.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @Secured({"ADMIN", "SUPER_ADMIN"})
    @GetMapping("")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {
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

    @Secured({"ADMIN", "SUPER_ADMIN"})
    @DeleteMapping("/{userID}")
    public ResponseEntity<Void> deleteUser(HttpServletRequest request, @PathVariable Long userID) {
        userService.deleteUser(request, userID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetails(request));
    }

    @Secured({"ADMIN", "SUPER_ADMIN"})
    @GetMapping("/{userID}")
    public ResponseEntity<UserDetailsDTO> getSpecificUserDetails(@PathVariable Long userID) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getSpecificUserDetails(userID));
    }

    @Secured("SUPER_ADMIN")
    @PatchMapping("/update-role/{userID}")
    public ResponseEntity<UserDetailsDTO> updateUserRole(@Valid @RequestBody RoleRequestDto roleData, @PathVariable Long userID) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserRole(roleData, userID));
    }
}
