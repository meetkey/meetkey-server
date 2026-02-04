package com.meetkey.server.global.security;


import com.meetkey.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, Principal {
    private final String memberId;
    private final String role;

    public Long getMemberId(){
        return Long.parseLong(memberId);
    }
    public CustomUserDetails(Member member){
        this.memberId = member.getId().toString();
        this.role = member.getRole().toString();
    }

    @Override
    public String getName() {
        return memberId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public @Nullable String getAuthority() {
                return role;
            }
        });
        return authorities;
    }

    // 비밀번호 사용 X
    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return memberId;
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
}
