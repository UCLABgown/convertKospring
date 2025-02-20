package team16.spring_project1.domain.member.Repository

import org.springframework.data.jpa.repository.JpaRepository
import team16.spring_project1.domain.member.Entity.Member
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Optional<Member>
    fun findByApiKey(apiKey: String): Optional<Member>
}
