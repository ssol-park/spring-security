package com.ssolpark.security.security;

import com.ssolpark.security.model.Member;
import lombok.Builder;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
@ToString
public class UserDetailsImpl implements UserDetails {

    private final String username;

    private final String password;

    public UserDetailsImpl(Member member) {
        this.username = member.getEmail();
        this.password = member.getPassword();
        System.out.println(" ############################# " + username + " ////// " + password);
    }

    @Builder
    public UserDetailsImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
