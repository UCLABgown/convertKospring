package team16.spring_project1.domain.member.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Repository.MemberRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;

    public long count() {
        return memberRepository.count();
    }

    public Member join(String username, String password) {
        Member member = new Member(username,password,UUID.randomUUID().toString());
        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String getAccessToken(Member member) {
        return authTokenService.getAccessToken(member);
    }

    public String getAuthToken(Member member) {
        return member.getApiKey() + " " + getAccessToken(member);
    }

    public Member getMemberFromAccessToken(String accessToken) {
        Map<String, Object> payload = authTokenService.payload(accessToken);

        if(payload == null) return null;

        long id = (long) payload.get("id");
        Member member;
		member = findById(id).get();

		return member;
    }
}
