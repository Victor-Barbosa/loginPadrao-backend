package com.qualrole.backend.auth.service;

import com.qualrole.backend.auth.exception.EmailNotFoundException;
import com.qualrole.backend.user.entity.SystemUser;
import com.qualrole.backend.user.repository.SystemUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.builder;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;

    public CustomUserDetailsService(SystemUserRepository systemUserRepository) {
        this.systemUserRepository = systemUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Usuário não encontrado com o e-mail: " + email));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        SystemUser user = systemUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com UUID: " + userId));

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(SystemUser user) {
        return builder()
                .username(user.getSystemUserId())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(user.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}