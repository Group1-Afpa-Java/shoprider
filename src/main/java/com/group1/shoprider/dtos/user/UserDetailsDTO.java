package com.group1.shoprider.dtos.user;

import com.group1.shoprider.models.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String role;
    private String email;
    private String address;

    public static UserDetailsDTO toUserDetailsDTO(User user) {
        return UserDetailsDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .build();
    }

    public static List<UserDetailsDTO> toUserDetailsDTOList(List<User> users) {
        List<UserDetailsDTO> results = new ArrayList<>();
        for (User user: users) {
            results.add(
                    UserDetailsDTO.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .username(user.getUsername())
                            .role(user.getRole().getName())
                            .email(user.getEmail())
                            .address(user.getAddress())
                            .build());
        }
        return results;
    }
}
