/**
 * Rest Controller for user related operations
 *  - list of users
 *  - change e-mail
 *  - add permissions to users
 */

package com.ua.riaw.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.ua.riaw.utils.error.exceptions.EntityNotFoundException;
import com.ua.riaw.utils.error.exceptions.UnauthorizedAccessException;
import com.ua.riaw.user.role.Role;
import com.ua.riaw.user.role.RoleEnum;
import com.ua.riaw.user.role.RoleRepository;
import com.ua.riaw.utils.views.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserDetailsServiceImpl userService;

    @Operation(summary = "Retrieves list of all users, except the user who made the request")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Gets list of all other users",
                    responseCode = "200",
                    content = { @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )}
            ),
            @ApiResponse(
                    description = "User who made request not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @GetMapping("/other_users")
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


    @Operation(description = "User changes its e-mail")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "User changes it e-mail with success",
                    responseCode = "200",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )}
            ),
            @ApiResponse(
                    description = "User who made request not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @PutMapping("/changeEmail")
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


    @Operation(description = "Get user details")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Retrieves successfully user's information",
                    responseCode = "200",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )}
            ),
            @ApiResponse(
                    description = "User who made request not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    @JsonView(Views.VisitingUser.class)
    public ResponseEntity<?> getUserInfo(@Param(value = "username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class, "username", username));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @Operation(description = "Gives administrator privileges to other user")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Gives permissions to other users",
                    responseCode = "200",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )}
            ),
            @ApiResponse(
                    description = "User who made request doesn't is not admin",
                    responseCode = "401",
                    content = @Content
            ),
            @ApiResponse(
                    description = "User who made request not found",
                    responseCode = "404",
                    content = @Content
            ),
            @ApiResponse(
                    description = "User to give permissions not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @PutMapping("/add_privilege")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.VisitingUser.class)
    public ResponseEntity<?> addPrivilegeToUser(
            @Param(value = "loggedUsername") String loggedUsername,
            @Param(value = "visitedUsername") String visitedUsername
    ) {
        /* get User (if present in DB) */
        User loggedUser = userRepository.findByUsername(loggedUsername).orElseThrow(() -> new EntityNotFoundException(User.class, "username", loggedUsername));

        /* get visited user object (if present in DB) */
        User visitedUser = userRepository.findByUsername(visitedUsername).orElseThrow(() -> new EntityNotFoundException(User.class, "username", visitedUsername));

        /* if user is admin -> give privilege */
        if (userService.userIsAdmin(loggedUser)) {
            Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN).orElseThrow(() -> new EntityNotFoundException(Role.class, "name", "ROLE_ADMIN"));

            /* get visited user roles */
            List<Role> newRoles = visitedUser.getRoles();
            /* add ADMIN role */
            newRoles.add(adminRole);
            visitedUser.setRoles(newRoles);
            visitedUser = userRepository.save(visitedUser);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(visitedUser);
        } else
            throw new UnauthorizedAccessException(User.class, loggedUsername, visitedUser.getId());
    }

}
