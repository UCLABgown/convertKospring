package team16.spring_project1.domain.member.Service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import team16.spring_project1.domain.member.Entity.Member
import team16.spring_project1.standard.util.Ut

@Service
class AuthTokenService {
    @Value("\${custom.jwt.secretKey}")
    private val jwtSecretKey: String? = null

    @Value("\${custom.accessToken.expirationSeconds}")
    private val accessTokenExpirationSeconds: Long = 0

    fun getAccessToken(member: Member): String {
        val id = member.id
        val username = member.username

        return Ut.jwt.toString(
            jwtSecretKey,
            accessTokenExpirationSeconds,
            java.util.Map.of<String, Any>("id", id, "username", username)
        )
    }

    fun payload(accessToken: String?): Map<String, Any?>? {
        val parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken) ?: return null

        val id = (parsedPayload["id"] as Int?)!!.toLong()
        val username = parsedPayload["username"] as String?

        return java.util.Map.of<String, Any?>("id", id, "username", username)
    }
}
