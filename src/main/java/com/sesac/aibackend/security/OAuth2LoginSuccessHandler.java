package com.sesac.aibackend.security;

import com.sesac.aibackend.domain.User;
import com.sesac.aibackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 구글 OAuth2 로그인 성공 직후를 가로채는 핸들러 (Day 4 B8).
 *
 * 흐름: 구글 인증 성공 → OIDC 사용자 정보로 우리 DB 사용자를 조회/생성 →
 * 폼 로그인과 동일한 방식으로 앱 자체 JWT를 발급 → 프런트로 토큰을 붙여 리다이렉트.
 * 인증 출처(폼/구글)가 달라도 이후 API는 동일한 앱 JWT로 동작합니다.
 */

// 승인된 리디렉션 URI 로 이동했을 때 handler
// chain 에 등록 해야함
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /** 토큰을 전달할 프런트 콜백 주소 (기본값은 React 개발 서버). */
    @Value("${app.oauth2.redirect-uri:http://localhost:5173/oauth/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // type cast 방식을 쓰는 게 맞나 애초에 method argument 단에서 oauth token 을 받는건 다른 동작인가 ?
        // Downcasting
        // handler 는 필터체인 단에서 동작하므로 인터페이스 시그니처 준수해야함
        // 가장 범용적인 Authentication 객체로 매개변수를 가짐
        // Authentication 을 구현한 하위 클래스 OAuth2AuthenticationToken
        // 다운캐스팅을 통해 하위 클래스 고유 메서드 호출
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        // OIDC sub: 구글이 보증하는 불변 고유 식별자. 이메일과 달리 변경·재사용되지 않습니다.
        String providerId = oidcUser.getSubject();
        // (provider, providerId)로 우리 DB 사용자를 조회하거나, 처음이면 신규 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> userRepository.save(User.oauthUser(email, provider, providerId)));

        // 폼 로그인과 동일한 방식으로 앱 자체 JWT 발급
        String token = jwtUtil.generate(user.getUsername(), user.getRole().name());

        // SPA라면 토큰을 프런트로 전달하여 리다이렉트
        response.sendRedirect(redirectUri + "?token=" + token);
    }
}
