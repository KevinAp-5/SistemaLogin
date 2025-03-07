package com.usermanager.manager.model.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    private String name;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roleList = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        if (this.role == UserRole.ADMIN)
            roleList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return roleList;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
