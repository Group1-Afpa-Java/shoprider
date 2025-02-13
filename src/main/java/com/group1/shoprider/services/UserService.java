package com.group1.shoprider.services;

import com.group1.shoprider.dtos.registration.AuthenticationRequest;
import com.group1.shoprider.dtos.registration.AuthenticationResponse;
import com.group1.shoprider.dtos.registration.RegisterRequest;
import com.group1.shoprider.dtos.user.UserRequestDTO;
import com.group1.shoprider.dtos.user.UserResponseDTO;
import com.group1.shoprider.exceptions.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final RepositoryRole roleRepository;
    private final RepositoryUser userRepository;
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
                .role(roleRepository.findByName("CLIENT").get())
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

    public User findUserMakingRequest(HttpServletRequest request) {
        // find user who is making the request (can be user, admin or super-admin)
        Long user_id = jwtService.extractUserIdFromToken(jwtService.getTokenString(request));
        return getUserByID(user_id);
    }


    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserResponseDTO.toUserResponseDTOList(users);
    }

    public void deleteUser(Long userID) {
        User foundUser = getUserByID(userID);
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


}
