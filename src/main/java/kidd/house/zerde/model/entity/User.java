package kidd.house.zerde.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import kidd.house.zerde.model.role.Authorities;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    @Email
    private String email;
    @Enumerated(value = EnumType.STRING)
    private Authorities authorities;
    @ManyToMany
    @JoinTable(name = "teacher_subjects", joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "subjects_id"))
    private List<Subject> subjects;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(authorities.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired()
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked()
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired()
    }

    @Override
    public boolean isEnabled() {
        return true;//UserDetails.super.isEnabled()
    }
}

