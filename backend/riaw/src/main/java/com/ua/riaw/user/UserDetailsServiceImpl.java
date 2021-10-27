package com.ua.riaw.user;

import com.ua.riaw.etlProcedure.ETL;
import com.ua.riaw.etlProcedure.ETLService;
import com.ua.riaw.security.payload.response.JwtResponse;
import com.ua.riaw.security.jwt.JwtUtils;
import com.ua.riaw.user.role.Role;
import com.ua.riaw.user.role.RoleEnum;
import com.ua.riaw.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ETLService etlService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsImpl.build(user);
    }


    /**
     * Creates 3 default users
     */

    public void createDefaultUser () {
        for (int i = 1; i <= 3; i++) {
            String username = "user00" + i;
            String email = username.concat("@mail.com");

            if (!(userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent())) {
                User user = new User(
                        username,
                        email,
                        encoder.encode(username)
                );
                Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElse(null);
                List<Role> roles = new ArrayList<>();

                if (userRole != null) {
                    roles.add(userRole);
                    user.setRoles(roles);
                }
                userRepository.save(user);
            }
        }
    }


    /**
     * Creates default Administrator
     */

    public void createDefaultAdmin() {
        String username = "admin";
        String email = username.concat("@mail.com");
        if (!userRepository.findByUsername(username).isPresent()) {
            User admin = new User(
                    username,
                    email,
                    encoder.encode(username)
            );
            List<Role> roles = new ArrayList<>(roleRepository.findAll());
            admin.setRoles(roles);
            userRepository.save(admin);
        }
    }


    /**
     * Gets user by username
     *
     * @param username user's username
     * @return user if found, null otherwise
     */

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    /**
     * Verifies if user is an administrator
     *
     * @param user User object
     * @return true if is admin, false otherwise
     */

    public boolean userIsAdmin(User user) {
        Role admin = roleRepository.findByName(RoleEnum.ROLE_ADMIN).orElse(null);
        if (admin != null) {
            return user.getRoles().contains(admin);
        }

        return false;
    }


    /**
     * Deletes user account
     *
     * @param user user to remove
     */

    public void deleteAccount(User user) {
        List<ETL> proceduresToRemove = new ArrayList<>();
        for (ETL etl : user.getEtls()) {
            if (etl.getUsers().size() == 1) {
                // remove etls if user is the only collaborator
                proceduresToRemove.add(etl);
            } else if (etl.getUsers().size() >= 2) {
                // remove collaborator from list of collaborators but keep procedure
                etlService.removeETLCollaborator(user, etl.getId());
            }
        }

        for (ETL etl : proceduresToRemove) etlService.deleteETLProcedure(etl.getId());
        user.setEtls(new ArrayList<>());
        user = userRepository.save(user);
        userRepository.delete(user);
    }


    public JwtResponse generateNewToken(User user) {
        String token = jwtUtils.generateNewJetToken(user.getUsername());

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        List<String> roles = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).collect(Collectors.toList());

        return new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
