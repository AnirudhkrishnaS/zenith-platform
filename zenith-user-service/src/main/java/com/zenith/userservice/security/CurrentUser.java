package com.zenith.userservice.security;

import com.zenith.userservice.enums.UserType;

import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Principal set in SecurityContext after JWT validation.
 * Holds userId, email, userType from the token.
 */
public class CurrentUser implements UserDetails {

    private final Long userId;
    private final String email;
    private final UserType userType;

    public CurrentUser(Long userId, String email, UserType userType) {
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Extract CurrentUser from Spring Security Authentication.
     */
    public static CurrentUser from(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser)) {
            return null;
        }
        return (CurrentUser) auth.getPrincipal();
    }
}
