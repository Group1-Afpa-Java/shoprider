package com.group1.shoprider.dtos.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDto {

    @NotBlank(message = "Role name cannot be empty")
    private String name;

}
