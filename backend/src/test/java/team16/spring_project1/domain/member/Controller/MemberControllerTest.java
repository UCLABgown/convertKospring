package team16.spring_project1.domain.member.Controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Service.MemberService;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("로그인")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/members/login")
                        .content("""
                                {
                                    "username": "admin",
                                    "password": "1234"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        Member member = memberService.findByUsername("admin").get();

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인되었습니다."))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.apiKey").value(member.getApiKey()))
                .andExpect(jsonPath("$.content.accessToken").exists());

        resultActions.andExpect(
                result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                    assertThat(accessTokenCookie.getSecure()).isTrue();

                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isNotBlank();
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    assertThat(apiKeyCookie.getSecure()).isTrue();
                }
        );
    }

    @Test
    @DisplayName("로그인 without username")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/members/login")
                        .content("""
                                {
                                    "username": "",
                                    "password": "1234"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("username-NotBlank-must not be blank"));
    }

    @Test
    @DisplayName("로그인 without password")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/members/login")
                        .content("""
                                {
                                    "username": "admin",
                                    "password": ""
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("password-NotBlank-must not be blank"));
    }

    @Test
    @DisplayName("로그인 wrong username")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/members/login")
                        .content("""
                                {
                                    "username": "user0",
                                    "password": "1234"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }

    @Test
    @DisplayName("로그인 wrong password")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/members/login")
                        .content("""
                                {
                                    "username": "admin",
                                    "password": "1111"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("username 조회 with admin")
    void t6() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        ResultActions resultActions = mvc
                .perform(get("/members/username")
                        .header("Authorization", "Bearer " + memberAuthToken)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findUsername"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(member.getUsername()));
    }

    @Test
    @DisplayName("username 조회 wrong access key")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/members/username")
                        .header("Authorization", "Bearer wrong-access-key")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findUsername"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }

    @Test
    @DisplayName("logout")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        delete("/members/logout")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다"))
                .andExpect(result -> {
                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isEmpty();
                    assertThat(accessTokenCookie.getMaxAge()).isEqualTo(0);
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                    assertThat(accessTokenCookie.getSecure()).isTrue();
                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEmpty();
                    assertThat(apiKeyCookie.getMaxAge()).isEqualTo(0);
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.isHttpOnly()).isTrue();
                    assertThat(apiKeyCookie.getSecure()).isTrue();
                });
    }
}
