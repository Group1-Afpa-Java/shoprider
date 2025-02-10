package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.role.RoleRequestDto;
import com.group1.shoprider.dtos.role.RoleResponseDto;
import com.group1.shoprider.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;
    @PostMapping("/add")
    public ResponseEntity<RoleResponseDto> addRole(@Valid @RequestBody RoleRequestDto roleRequestDTO) {
        RoleResponseDto roleResponseDto = roleService.addRole(roleRequestDTO);
        return new ResponseEntity<>(roleResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto roleRequestDTO) {
        RoleResponseDto updatedRole = roleService.updateRole(id, roleRequestDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role with ID " + id + " has been deleted successfully.");
    }




}
