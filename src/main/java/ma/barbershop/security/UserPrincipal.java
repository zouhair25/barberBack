package ma.barbershop.security;

import lombok.Getter;
import ma.barbershop.domain.entity.User;
import ma.barbershop.domain.entity.UserCentreSoin;
import ma.barbershop.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Role role;
    private final String firstname;
    private final String lastname;
    private final List<UserCentreSoin>userCentreSoins;
    private final boolean active;
    private UserCentreSoin userCentreSoin;
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.active = user.isActive();
        this.firstname = user.getFirstName();
        this.lastname = user.getLastName();
        this.userCentreSoins = user.getUserCentreSoins() != null
                ? new ArrayList<>(user.getUserCentreSoins())
                : new ArrayList<>();
        this.userCentreSoin = this.userCentreSoins.isEmpty() ? null : this.userCentreSoins.getFirst();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }



    @Override
    public String toString() {
        return "UserPrincipal{id=" + id + ", email=" + email + ", role=" + role + "}";
    }
}
