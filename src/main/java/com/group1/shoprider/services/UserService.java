package com.group1.shoprider.services;

import com.group1.shoprider.dtos.registration.AuthenticationRequest;
import com.group1.shoprider.dtos.registration.AuthenticationResponse;
import com.group1.shoprider.dtos.registration.RegisterRequest;
import com.group1.shoprider.exceptions.InvalidUsernameOrPasswordException;
import com.group1.shoprider.exceptions.UserAlreadyExistsException;
import com.group1.shoprider.models.User;
import com.group1.shoprider.repository.RepositoryRole;
import com.group1.shoprider.repository.RepositoryUser;
import com.group1.shoprider.services.auth.JWTService;
import com.group1.shoprider.services.auth.UserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void register(RegisterRequest request) {
        Optional<User> foundUser = userRepository.findByUsername(request.getUsername());
        if (foundUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("Username: <%s> is not available.", request.username));
        }
        User user = User.builder()
                .username(request.username)
                .password(passwordEncoder.encode(request.password))
                .role(roleRepository.findByName("CLIENT").get())
                .build();
        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
            );
            User user = (User) userDetailsService.loadUserByUsername(request.getUsername());
            authenticationManager.authenticate(authenticationToken);
            final String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        }catch (AuthenticationException e) {
            throw new InvalidUsernameOrPasswordException("Invalid username/password supplied");
        }
    }
}
