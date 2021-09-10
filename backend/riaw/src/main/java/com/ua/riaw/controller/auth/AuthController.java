package com.ua.riaw.controller.auth;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riaw.error.exceptions.EntityNotFoundException;
import com.ua.riaw.model.auth.Role;
import com.ua.riaw.model.auth.RoleEnum;
import com.ua.riaw.model.auth.User;
import com.ua.riaw.payload.request.LoginRequest;
import com.ua.riaw.payload.request.SignupRequest;
import com.ua.riaw.payload.response.JwtResponse;
import com.ua.riaw.payload.response.MessageResponse;
import com.ua.riaw.repository.auth.RoleRepository;
import com.ua.riaw.repository.auth.UserRepository;
import com.ua.riaw.security.jwt.JwtUtils;
import com.ua.riaw.security.services.UserDetailsImpl;
import com.ua.riaw.views.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/users")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        /* user name already exists */
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("ERROR: Username is already taken!"));
        }

        /* email already exists */
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("ERROR: E-mail is already taken"));
        }

        /* create new user */
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword())
        );

        Set<String> strRoles = signupRequest.getRole();
        List<Role> roles = new ArrayList<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
                        roles.add(adminRole);

                    default:
                        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("ERROR: Role is not found"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(
                item -> item.getAuthority()
        ).collect(Collectors.toList());

        return new ResponseEntity<>(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles),
                HttpStatus.OK);
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    @JsonView(Views.UserList.class)
    public ResponseEntity<?> getAllUsers(@Param(value = "username") String username) {
        // get list of users
        List<User> users = userRepository.findAll();

        // remove user who made request
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class, "username", username));
        users.remove(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PutMapping("changeEmail")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changeEmail(
            @Param(value = "username") String username,
            @Param(value = "newEmail") String newEmail)
    {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class, "username", username));
        user.setEmail(newEmail);
        user = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @GetMapping("user")
    @PreAuthorize("hasRole('USER')")
    @JsonView(Views.VisitingUser.class)
    public ResponseEntity<?> getUserInfo(@Param(value = "username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class, "username", username));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
