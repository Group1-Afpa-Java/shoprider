package com.group1.shoprider.dtos.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    public String firstName;
    @NotBlank
    public String lastName;
    @NotBlank
    public String userName;
    // @Min(value = 8, message="Password must not be less than 8 characters.")
    @NotBlank
    public String password;
    @Email
    public String email;
    @NotBlank
    public String address;
}
