package com.example.demo.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final Collection<? extends GrantedAuthority> authorities;

    @Getter
    private final User user;

    public CustomUserDetails(User byUsername) {
        this.user = byUsername;

        List<GrantedAuthority> auths = new ArrayList<>();
        for (UserRole role : byUsername.getRoles()) {
            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.user.blocked;
    }

    @Override
    public boolean isEnabled() {
        return this.user.active;
    }
}
