package team16.spring_project1.domain.member.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team16.spring_project1.domain.member.Entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByApiKey(String apiKey);
}
