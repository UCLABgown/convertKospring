package team16.spring_project1.domain.member.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import team16.spring_project1.global.jpa.entity.BaseTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {
    @Column(unique = true, length = 30)
    private String username;

    @Column(length = 50)
    private String password;

    @Column(unique = true)
    private String apiKey;

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isAdmin() {
        return "admin".equals(username);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin())
            authorities.add("ROLE_ADMIN");

        return authorities;
    }
}
