package com.akshayan.authservice.service;

import com.akshayan.authservice.model.ForumUser;
import com.akshayan.authservice.repository.ForumUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ForumUserRepository forumUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ForumUser> userOptional= forumUserRepository.findByUsername(username);
        ForumUser forumUser = userOptional.orElseThrow(()-> new UsernameNotFoundException("User Not Found with username: "+username));
        return new org.springframework.security.core.userdetails.User(
                forumUser.getUsername(),
                forumUser.getPassword(),
                forumUser.isEnabled(),
                true,
                true,
                true,
                getAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
