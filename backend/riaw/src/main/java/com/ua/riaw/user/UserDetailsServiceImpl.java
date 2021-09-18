package com.ua.riaw.user;

import com.ua.riaw.user.role.Role;
import com.ua.riaw.user.role.RoleEnum;
import com.ua.riaw.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsImpl.build(user);
    }


    /**
     * Creates a default user
     */

    public void createDefaultUser () {
        if (!userRepository.findByUsername("user123").isPresent()) {
            User user = new User(
                    "user123",
                    "user123@mail.com",
                    encoder.encode("user123")
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


    /**
     * Creates default Administrator
     */

    public void createDefaultAdmin() {
        if (!userRepository.findByUsername("admin123").isPresent()) {
            User admin = new User(
                    "admin123",
                    "admin123@gmail.com",
                    encoder.encode("admin123")
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
}
