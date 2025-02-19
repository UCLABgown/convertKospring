package team16.spring_project1.global.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import team16.spring_project1.global.apiResponse.ApiResponse;
import team16.spring_project1.standard.util.Ut;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomAuthenticationFilter customAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/h2-console/**")
					.permitAll()
					.requestMatchers(HttpMethod.POST, "/products")
					.hasRole("ADMIN")
					//.requestMatchers(HttpMethod.GET, "/order")
					//.hasRole("ADMIN")
					.requestMatchers(HttpMethod.PUT, "/products/{id}")
					.hasRole("ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/products/{id}")
					.hasRole("ADMIN")
					.anyRequest()
					.permitAll()
			)
			.headers(
				headers ->
					headers
						.frameOptions(frameOptions ->
							frameOptions.sameOrigin()
						)
			).cors(cors -> corsConfigurationSource())
			.csrf(csrf ->
				csrf.disable()
			)
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(
				exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(
						(request, response, authException) -> {
							response.setContentType("application/json;charset=UTF-8");

							response.setStatus(401);
							response.getWriter().write(
								Ut.json.toString(
									ApiResponse.failure("사용자 인증정보가 올바르지 않습니다.")
								)
							);
						}
					)
					.accessDeniedHandler(
						(request, response, accessDeniedException) -> {
							response.setContentType("application/json;charset=UTF-8");

							response.setStatus(403);
							response.getWriter().write(
								Ut.json.toString(
									ApiResponse.failure("권한이 없습니다.")
								)
							);
						}
					)
			);

		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용할 오리진 설정
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

		// 허용할 HTTP 메서드 설정
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

		// 자격 증명 허용 설정
		configuration.setAllowCredentials(true);

		// 허용할 헤더 설정
		configuration.setAllowedHeaders(Arrays.asList("*"));

		// CORS 설정을 소스에 등록
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
