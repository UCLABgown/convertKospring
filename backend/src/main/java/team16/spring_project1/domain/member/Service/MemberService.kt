package team16.spring_project1.domain.member.Service

import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import team16.spring_project1.domain.member.Entity.Member
import team16.spring_project1.domain.member.Repository.MemberRepository
import java.util.*

@Service
@RequiredArgsConstructor
class MemberService (
   val memberRepository: MemberRepository,
    val authTokenService: AuthTokenService
){


    fun count(): Long {
        return memberRepository.count()
    }

    fun join(username: String, password: String): Member {
        val member = Member(username, password, UUID.randomUUID().toString())
        return memberRepository.save(member)
    }

    fun findByUsername(username: String?): Optional<Member?> {
        return memberRepository.findByUsername(username)
    }

    fun findById(id: Long): Optional<Member> {
        return memberRepository.findById(id)
    }

    fun findByApiKey(apiKey: String): Optional<Member> {
        return memberRepository.findByApiKey(apiKey)
    }

    fun getAccessToken(member: Member): String {
        return authTokenService.getAccessToken(member)
    }

    fun getAuthToken(member: Member): String {
        return "${member.apiKey} ${getAccessToken(member)}"
    }

    fun getMemberFromAccessToken(accessToken: String?): Member? {
        val payload = authTokenService.payload(accessToken) ?: return null

        val id = payload["id"] as Long
        val member = findById(id).get()

        return member
    }
}
