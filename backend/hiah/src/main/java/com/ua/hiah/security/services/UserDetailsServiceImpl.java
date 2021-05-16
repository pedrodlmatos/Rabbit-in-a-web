package com.ua.hiah.security.services;

import com.ua.hiah.model.auth.Role;
import com.ua.hiah.model.auth.RoleEnum;
import com.ua.hiah.model.auth.User;
import com.ua.hiah.repository.auth.RoleRepository;
import com.ua.hiah.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

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

    public void createDefaultAdmin() {
        if (!userRepository.findByUsername("admin123").isPresent()) {
            User admin = new User(
                    "admin123",
                    "admin123@gmail.com",
                    encoder.encode("admin123")
            );
            Set<Role> roles = new HashSet<>(roleRepository.findAll());
            admin.setRoles(roles);
            userRepository.save(admin);
        }

    }
}
