package team16.spring_project1.domain.member.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Service.MemberService;
import team16.spring_project1.global.apiResponse.ApiResponse;
import team16.spring_project1.global.exceptions.PasswordMismatchException;
import team16.spring_project1.global.rq.Rq;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "MemberController", description = "회원 관리 API")
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

    record MemberLoginReqBody(
            @NotBlank
            String username,

            @NotBlank
            String password
    ) {}

    record MemberLoginResBody(
            @NonNull
            String apiKey,
            @NonNull
            String accessToken
    ) {}

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "User Login", description = "apiKey, accessToken을 발급합니다. 해당 토큰들은 쿠키(HTTP-ONLY)로도 전달됩니다.")
    public ResponseEntity<ApiResponse<MemberLoginResBody>> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {
        Member member = memberService
                .findByUsername(reqBody.username)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        if(!member.matchPassword(reqBody.password))
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");

        String accessToken = memberService.getAccessToken(member);

        rq.setCookie("accessToken", accessToken);
        rq.setCookie("apiKey", member.getApiKey());

        return ResponseEntity.ok(ApiResponse.success(
                "로그인되었습니다.",
                new MemberLoginResBody(
                        member.getApiKey(),
                        accessToken
                )
        ));
    }

    @GetMapping("/username")
    @Transactional(readOnly = true)
    @Operation(summary = "Find Username", description = "사용자의 username을 조회합니다.")
    public ResponseEntity<ApiResponse<String>> findUsername() {
        Member member = rq.getMember();

        if(member == null)
            throw new NullPointerException("존재하지 않는 사용자입니다.");

        return ResponseEntity.ok(ApiResponse.success(member.getUsername()));
    }

    @DeleteMapping("/logout")
    @Transactional(readOnly = true)
    @Operation(summary = "User Logout", description = "apiKey, accessToken을 제거합니다.")
    public ResponseEntity<ApiResponse<String>> logout() {
        rq.deleteCookie("accessToken");
        rq.deleteCookie("apiKey");

        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다"));
    }
}
