package com.group1.shoprider.services;

import com.group1.shoprider.dtos.role.RoleRequestDto;
import com.group1.shoprider.dtos.role.RoleResponseDto;
import com.group1.shoprider.exceptions.RoleNotFoundException;
import com.group1.shoprider.models.Role;
import com.group1.shoprider.repository.RepositoryRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RepositoryRole repositoryRole;

    public RoleResponseDto addRole(RoleRequestDto roleRequestDto){
        Role role = new Role();
        role.setName(roleRequestDto.getName());
        Role savedRole = repositoryRole.save(role);
        return new RoleResponseDto(savedRole.getId(), savedRole.getName());
    }
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = repositoryRole.findAll();
        return roles.stream()
                .map(role -> new RoleResponseDto(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }
    public RoleResponseDto getRoleById(Long id) {
        Role role = repositoryRole.findById(id)  // Find role by ID
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return new RoleResponseDto(role.getId(), role.getName());
    }
    public RoleResponseDto updateRole(Long id,RoleRequestDto roleRequestDto){
        Role role = repositoryRole.findById(id)  // Find existing role
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));  // Handle not found
        role.setName(roleRequestDto.getName());  // Update role name
        Role updatedRole = repositoryRole.save(role);  // Save updated role
        return new RoleResponseDto(updatedRole.getId(), updatedRole.getName());  // Convert to response DTO
    }
    public void deleteRole(Long id){
        if (!repositoryRole.existsById(id)) {  // Check if role exists
            throw new RuntimeException("Role not found with id: " + id);
        }
        repositoryRole.deleteById(id);  // Delete role by ID
    }

    public Role getRoleByName(String name) {
        Optional<Role> role = repositoryRole.findByName(name);
        if (role.isEmpty()) {
            throw new RoleNotFoundException(String.format("Role with name: %s was not found", name));
        }
        return role.get();
    }
}
