package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.role.RoleRequestDto;
import com.group1.shoprider.dtos.role.RoleResponseDto;
import com.group1.shoprider.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;
    @Secured({"ADMIN", "SUPER-ADMIN"})
    @PostMapping("/add")
    public ResponseEntity<RoleResponseDto> addRole(@Valid @RequestBody RoleRequestDto roleRequestDTO) {
        RoleResponseDto roleResponseDto = roleService.addRole(roleRequestDTO);
        return new ResponseEntity<>(roleResponseDto, HttpStatus.CREATED);
    }
    @Secured({"ADMIN", "SUPER-ADMIN"})
    @PutMapping("/update/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto roleRequestDTO) {
        RoleResponseDto updatedRole = roleService.updateRole(id, roleRequestDTO);
        return ResponseEntity.ok(updatedRole);
    }
    @Secured({"ADMIN", "SUPER-ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
        RoleResponseDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }
    @Secured({"ADMIN", "SUPER-ADMIN"})
    @GetMapping("")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Secured({"ADMIN", "SUPER-ADMIN"})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role with ID " + id + " has been deleted successfully.");
    }

}
