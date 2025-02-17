package com.group1.shoprider.services;

import com.group1.shoprider.dtos.registration.AuthenticationRequest;
import com.group1.shoprider.dtos.registration.AuthenticationResponse;
import com.group1.shoprider.dtos.registration.RegisterRequest;
import com.group1.shoprider.dtos.role.RoleRequestDto;
import com.group1.shoprider.dtos.user.UserDetailsDTO;
import com.group1.shoprider.dtos.user.UserRequestDTO;
import com.group1.shoprider.dtos.user.UserResponseDTO;
import com.group1.shoprider.enums.UserRoles;
import com.group1.shoprider.exceptions.*;
import com.group1.shoprider.models.Role;
import com.group1.shoprider.models.User;
import com.group1.shoprider.repository.RepositoryRole;
import com.group1.shoprider.repository.RepositoryUser;
import com.group1.shoprider.services.auth.JWTService;
import com.group1.shoprider.services.auth.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final RepositoryRole roleRepository;
    private final RepositoryUser userRepository;
    private final RoleService roleService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public User register(RegisterRequest request) {
        Optional<User> foundUser = userRepository.findByUsername(request.getUserName());
        if (foundUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("Username: <%s> is not available.", request.getUserName()));
        }
        User user = User.builder()
                .username(request.getUserName())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.password))
                .role(roleRepository.findByName(UserRoles.CLIENT.name()).get())
                .build();
        return userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    request.getUserName(),
                    request.getPassword()
            );
            User user = (User) userDetailsService.loadUserByUsername(request.getUserName());
            authenticationManager.authenticate(authenticationToken);
            final String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        }catch (AuthenticationException e) {
            throw new InvalidUsernameOrPasswordException("Invalid username/password supplied");
        }
    }

    public UserDetailsDTO getUserDetails(HttpServletRequest request) {
        String token = jwtService.getTokenString(request);
        User userMakingRequest = getUserByID(jwtService.extractUserIdFromToken(token));
        return UserDetailsDTO.builder()
                .id(userMakingRequest.getId())
                .username(userMakingRequest.getUsername())
                .role(userMakingRequest.getRole().getName())
                .firstName(userMakingRequest.getFirstName())
                .lastName(userMakingRequest.getLastName())
                .email(userMakingRequest.getEmail())
                .address(userMakingRequest.getAddress())
                .build();
    }

    public UserDetailsDTO getSpecificUserDetails(Long userID) {
        User foundUser = getUserByID(userID);
        return UserDetailsDTO.builder()
                .id(foundUser.getId())
                .username(foundUser.getUsername())
                .role(foundUser.getRole().getName())
                .firstName(foundUser.getFirstName())
                .lastName(foundUser.getLastName())
                .email(foundUser.getEmail())
                .address(foundUser.getAddress())
                .build();
    }


    public User getUserByID(Long userID) {
        Optional<User> foundUser = userRepository.findById(userID);
        if (foundUser.isPresent()) {
            return foundUser.get();
        }
        throw new UserNotFoundException(String.format("User with id <%d> was not found.", userID));
    }

    public boolean userWithEmailExists(String email, Long user_id) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            return !user.getId().equals(user_id);
        }
        return false;
    }

    public boolean userWithUsernameExists(String username, Long user_id) {
        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            return !user.getId().equals(user_id);
        }
        return false;
    }

    public String getUserRoleFromToken(HttpServletRequest request) {
        String token = jwtService.getTokenString(request);
        User user = getUserByID(jwtService.extractUserIdFromToken(token));
        return user.getRole().getAuthority();
    }

    public User findUserMakingRequest(HttpServletRequest request) {
        // find user who is making the request (can be user, admin or super-admin)
        Long user_id = jwtService.extractUserIdFromToken(jwtService.getTokenString(request));
        return getUserByID(user_id);
    }

    public List<UserDetailsDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserDetailsDTO.toUserDetailsDTOList(users);
    }

    public void deleteUser(HttpServletRequest request, Long userID) {
        User userToBeDeleted = getUserByID(userID);
        String userMakingRequestRole = getUserRoleFromToken(request);
        if (userMakingRequestRole.equals(UserRoles.ADMIN.name())
                && userToBeDeleted.getRole().getName().equals(UserRoles.SUPER_ADMIN.name()
        )) {
            throw new ActionNotPermitted(String.format(
                    "Role <%s> cannot delete User with role <%s>", userMakingRequestRole, UserRoles.SUPER_ADMIN.name()
            ));
        }
        userRepository.deleteById(userID);
    }

    public UserResponseDTO updateUser(UserRequestDTO userData, HttpServletRequest request, Long otherUserID){
        User userToUpdate;
        User userMakingRequest = findUserMakingRequest(request);
        String role = userMakingRequest.getRole().getAuthority();

        // user updates his/her data
        if (otherUserID == null) {
            // check if email field in the request already belongs to another user
            if (userWithEmailExists(userData.getEmail(), userMakingRequest.getId())){
                throw new EmailAlreadyExistsException("Bad Request: Please use another email.");
            }
            // check if username field in the request already belongs to another user
            if (userWithUsernameExists(userData.getUserName(), userMakingRequest.getId())){
                throw new EmailAlreadyExistsException("Bad Request: Please use another username.");
            }
            userToUpdate = userMakingRequest;
        } else {
            // check if email field in the request already belongs to another user
            if (userWithEmailExists(userData.getEmail(), otherUserID)){
                throw new EmailAlreadyExistsException("Bad Request: Please use another email.");
            }
            // check if username field in the request already belongs to another user
            if (userWithUsernameExists(userData.getUserName(), otherUserID)){
                throw new EmailAlreadyExistsException("Bad Request: Please use another username.");
            }
            if (role.equals("ADMIN")) {
                User foundUser = getUserByID(otherUserID);
                if (foundUser.getRole().getName().equals("SUPER-ADMIN")) {
                    throw new ActionNotPermitted("Role <ADMIN> cannot perform this action.");
                }
                userToUpdate = foundUser;
            }
            else if (role.equals("SUPER-ADMIN")) {
                userToUpdate = getUserByID(otherUserID);
            } else {
                throw new ActionNotPermitted("Role <CLIENT> cannot perform this action.");
            }
        }
        userToUpdate.setFirstName(userData.getFirstName());
        userToUpdate.setLastName(userData.getLastName());
        userToUpdate.setUsername(userData.getUserName());
        userToUpdate.setAddress(userData.getAddress());
        userToUpdate.setEmail(userData.getEmail());
        return UserResponseDTO.toUserResponseDTO(userRepository.save(userToUpdate));
    }

    public UserDetailsDTO updateUserRole(RoleRequestDto roleData, Long userID) {
        User foundUser = getUserByID(userID);
        Role foundRole = roleService.getRoleByName(roleData.getName());
        foundUser.setRole(foundRole);
        userRepository.save(foundUser);
        return UserDetailsDTO.builder()
                .id(foundUser.getId())
                .firstName(foundUser.getFirstName())
                .lastName(foundUser.getLastName())
                .username(foundUser.getUsername())
                .role(foundRole.getName())
                .email(foundUser.getEmail())
                .address(foundUser.getAddress())
                .build();
    }
}
