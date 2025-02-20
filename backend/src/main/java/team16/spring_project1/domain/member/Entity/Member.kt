package team16.spring_project1.domain.member.Entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import team16.spring_project1.global.jpa.entity.BaseTime
import java.util.ArrayList

@Entity
class Member : BaseTime {
    @Column(unique = true, length = 30)
    lateinit var username:String
    @Column(length = 50)
    lateinit var  password:String;
    @Column(unique = true)
    lateinit var apiKey:String;
    constructor()
    constructor(username:String, password: String, apiKey: String) {
        this.username = username
        this.password = password
        this.apiKey = apiKey
    }

    fun matchPassword(password:String):Boolean {
        return this.password.equals(password)
    }

    val isAdmin: Boolean
        get() = "admin".equals(username);

    val authoritiesAsStringList: MutableList<String>
        get() {
            val authorities: MutableList<String> = ArrayList()
            if (isAdmin) authorities.add("ROLE_ADMIN")
            return authorities
        }
    val authorities: MutableList<SimpleGrantedAuthority>?
        get() = authoritiesAsStringList
            .stream()
            .map { role: String -> SimpleGrantedAuthority(role) }
            .toList()
}

