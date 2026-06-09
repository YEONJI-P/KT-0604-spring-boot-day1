### 1. 에러 발생 메커니즘 (에러가 감추어지는 현상)

브라우저 네트워크 탭과 스프링 서버 로그를 연결해 보면 다음과 같은 흐름으로 예외가 발생하고 있습니다.

1. 카카오 로그인 진행 및 인가 코드 수신:                                                                                                                                        
   브라우저가 카카오 인증을 완료하고 백엔드의  /login/oauth2/code/kakao?code=... 로 GET 요청을 보냈습니다.
2. 백엔드 OAuth2 인증 실패 (진짜 에러 발생):                                                                                                                                    
   Spring Security의  OAuth2LoginAuthenticationFilter 가 인가 코드를 받아 카카오 서버와 access token 교환 및 사용자 정보를 조회하는 과정에서 **인증                                
   에러(AuthenticationException)**가 발생했습니다.
3. 실패 핸들러 작동 및 리다이렉트:                                                                                                                                              
   OAuth2 로그인에 실패하면, Spring Security의 기본 실패 핸들러( SimpleUrlAuthenticationFailureHandler )는 브라우저를  /login?error  경로로 리다이렉트(302 Found)시킵니다.
4.  GET /login  요청 발생 및 405 에러:                                                                                                                                          
    브라우저가 리다이렉트된 주소인  GET http://localhost:8080/login?error 로 요청을 보냅니다.                                                                                       
    그러나 현재 AuthController.java에는  @PostMapping("/login") 만 존재하고,  GET /login 에 대한 컨트롤러 매핑이 없습니다.
5. 405 Method Not Allowed 발생 및 500으로 마스킹:                                                                                                                               
   스프링 MVC는  HttpRequestMethodNotSupportedException  (Request method 'GET' is not supported)을 던집니다. 이 예외가 GlobalExceptionHandler.java의 fallback 핸들러( Exception.class
   )로       
   들어가면서  log.error("unexpected", e) 를 남기고 500(INTERNAL) JSON 응답을 리턴하게 된 것입니다.

│ [!IMPORTANT]                                                                                                                                                                  
│ 즉, ** Request method 'GET' is not supported  에러는 카카오 로그인 인증 실패로 인해  /login?error 로 리다이렉트되는 과정에서 나타난 2차 예외(결과)**이며, 진짜 원인은 **그    
│ 이전에 발생한 "카카오 OAuth2 인증 실패"**입니다.                                                                                                                              
──────
### 2. 카카오 OAuth2 인증이 실패하는 주된 원인 (1차 원인)

현재 설정 파일 application.yaml의 카카오 설정을 보면 다음과 같은 부분에서 인증 실패가 일어났을 가능성이 매우 높습니다.

#### 원인 A:  openid  스코프 설정 불일치

• application.yaml에  openid  스코프가 등록되어 있습니다.                                                                                                                        
• 카카오 디벨로퍼스 콘솔( [내 애플리케이션] > [카카오 로그인] > [OpenID Connect] ) 설정이 **[ON]**으로 켜져 있지 않다면, 카카오 인증 서버는 ID 토큰을 발급하지 못하므로 Spring
Security단에서  invalid_scope  등으로 인증을 실패 처리합니다.                                                                                                                   
• 해결 제안: 카카오 콘솔에서 OpenID Connect를 활성화하거나, 일반 OAuth2 스코프만 사용하려면  application.yaml 에서  openid  스코프를 제거해 보십시오.

#### 원인 B:  client-secret  설정 불일치

• application.yaml에  client-secret: ${KAKAO_CLIENT_SECRET:changeme-kakao-client-secret} 로 기본값이 지정되어 있습니다.                                                          
• 만약 카카오 디벨로퍼스 콘솔( [내 애플리케이션] > [카카오 로그인] > [보안] )에서  Client Secret 을 생성 및 사용 설정해두지 않았거나 생성된 값과 환경변수 값이 다르면, 카카오  
토큰 서버에서  KOE010  (Bad client secret) 에러를 반환하여 로그인이 실패합니다.                                                                                                 
• 해결 제안: 카카오 콘솔에서 Client Secret 사용 상태를 확인하시고, 사용하지 않는다면 설정 파일에서  client-secret  항목을 지우거나 사용한다면 환경변수  KAKAO_CLIENT_SECRET 에
정확한 값을 주입해야 합니다.                                                                                                                                                    
──────
### 3. 실제 카카오 에러 디버깅 및 해결 가이드

진짜 에러 원인을 명확하게 확인하기 위해 아래 방법들을 시도해 보실 수 있습니다.

#### 방법 1: Spring Security 로그 레벨 변경

스프링의 보안 관련 로그를 디버깅 레벨로 설정하면,  /login?error 로 리다이렉트되기 직전에 스프링이 카카오 인증 서버와 통신하다가 발생한 진짜 예외(예:                            
OAuth2AuthorizationException  등)의 상세 에러 코드(예:  KOE205 ,  KOE010 )를 로그에서 볼 수 있습니다.

• application.yaml에 다음 설정을 추가하고 서버를 재시작한 뒤 로그인을 시도해 보세요.                                                                                             
logging:                                                                                                                                                                      
level:                                                                                                                                                                      
org.springframework.security: DEBUG


#### 방법 2: Security Config에 failureHandler 추가

인증 실패 시 405 에러가 나는  /login?error 로 가도록 두는 대신, 실패 에러 내용을 프론트엔드로 바로 리다이렉트하거나 JSON으로 응답하도록 SecurityConfig.java 설정을 커스텀하면 진짜
에러를 쉽게 추적할 수 있습니다.

• 예시:                                                                                                                                                                        
.oauth2Login(oauth2 -> oauth2                                                                                                                                                 
.successHandler(oAuth2LoginSuccessHandler)                                                                                                                                
.failureHandler((request, response, exception) -> {                                                                                                                       
// 예: 프론트엔드 콜백 주소로 진짜 에러 메시지를 쿼리 파라미터에 담아 전송                                                                                            
response.sendRedirect("http://localhost:5173/oauth/callback?error=" + exception.getMessage());                                                                        
})                                                                                                                                                                        
)                                    


    Authentication failed with provider OidcAuthorizationCodeAuthenticationProvider since [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0   
Access Token Response: 401 : [no body]

이 로그를 보면 스프링 시큐리티가 OIDC(OpenID Connect) 방식( OidcAuthorizationCodeAuthenticationProvider )으로 카카오 서버에 토큰을 요청했는데, 카카오 측에서  401 Unauthorized  
응답을 보내어 인증에 실패한 상황입니다.

이 문제는 카카오 디벨로퍼스 설정과  application.yaml 의 클라이언트 설정이 불일치하여 발생합니다. 해결을 위해 다음 두 가지 방법 중 하나를 선택하여 설정을 맞추어야 합니다.       
──────
### 해결 방법 1: 카카오 OpenID Connect(OIDC)를 사용하지 않는 경우 (추천)

카카오는 일반 OAuth 2.0 방식을 주로 사용합니다. OIDC를 비활성화하려면 아래 단계를 진행하십시오.

1.  application.yaml  수정:                                                                                                                                                     
    scope  항목에서  openid 를 삭제합니다. 이렇게 하면 스프링 시큐리티가 OIDC 대신 일반 OAuth 2.0 프로바이더를 사용하여 토큰을 요청하게 됩니다.                                    
    # application.yaml 예시                                                                                                                                                       
    kakao:                                                                                                                                                                        
    client-id: ${KAKAO_API_KEY:changeme-kakao-client-id}
    # client-secret이 콘솔 보안 메뉴에서 비활성화 상태라면 아래 항목들은 생략하거나 주석 처리
    # client-secret: ${KAKAO_CLIENT_SECRET:changeme-kakao-client-secret}
    # client-authentication-method: client_secret_post
    authorization-grant-type: authorization_code                                                                                                                                
    redirect-uri: "http://localhost:8080/login/oauth2/code/kakao"                                                                                                               
    client-name: Kakao                                                                                                                                                          
    scope:
    # - openid  <-- ❌ 이 부분을 제거합니다.
    - account_email
    - profile_nickname

2. 카카오 디벨로퍼스 콘솔 보안 설정 확인:                                                                                                                                       
   •  내 애플리케이션 > 제품 설정 > 카카오 로그인 > 보안  메뉴에서 Client Secret 기능을 활성화하지 않았다면,  application.yaml 에서도  client-secret 과  client-authentication-
   method  관련 설정을 지워주시는 것이 안전합니다. (기본값인  changeme-kakao-client-secret 이 전송되면 값 불일치로 401 에러가 날 수 있습니다.)

──────
### 해결 방법 2: 카카오 OpenID Connect(OIDC)를 사용하려는 경우

만약 OIDC를 반드시 활성화해서 ID 토큰을 발급받아야 하는 상황이라면 아래 설정을 확인하십시오.

1. 카카오 디벨로퍼스 콘솔 OIDC 활성화:                                                                                                                                          
   •  내 애플리케이션 > 제품 설정 > 카카오 로그인 > OpenID Connect  메뉴로 이동하여 사용 설정을 **[ON]**으로 변경합니다. (이 설정이 OFF인 상태에서  openid  스코프를 요청하면
   401 에러가 납니다.)
2. Client Secret 정합성 확인:                                                                                                                                                   
   • 만약 보안 메뉴에서  Client Secret 을 사용 설정하셨다면, 실제 발급받은 비밀키 값을 환경변수  KAKAO_CLIENT_SECRET 에 정확히 주입하셔야 합니다.                             
   • 사용 설정하지 않았다면 콘솔에서 기능을 끄고  application.yaml 에서도 해당 설정을 삭제해야 합니다.

──────
### ⚠️ 다음 단계에서 예상되는 에러 (미리 알아두셔야 할 사항)

위 설정을 통해 카카오로부터 성공적으로 토큰을 받아오게 되면(401 에러 해결), 그 이후에 ** ClassCastException **이 발생할 가능성이 높습니다.

이유는 현재 OAuth2LoginSuccessHandler.java가 다음과 같이 구글 전용인  OidcUser  타입으로만 강제 변환하도록 구현되어 있기 때문입니다.

    OidcUser oidcUser = (OidcUser) authentication.getPrincipal(); // ⚠️ 카카오 로그인(DefaultOAuth2User) 시 ClassCastException 발생 예정                                           

따라서 401 에러를 해결하신 후, 로그인 성공 시 정상 작동하게 하려면 해당 핸들러(OAuth2LoginSuccessHandler.java)에서  OidcUser  대신 상위 인터페이스인  OAuth2User 를 사용하여
로그인 플랫폼(     
registrationId 가  "google" 인지  "kakao" 인지)에 맞춰 이메일과 고유 식별자(ID)를 동적으로 추출하도록 수정해주셔야 정상적으로 연동이 완료됩니다.


### 2. OIDC 사용 시  jwk-set-uri  누락 문제

현재  scope 에  openid 가 들어가 있고 카카오 콘솔에서도 OIDC를 켜두셨다면, 스프링 시큐리티 OIDC 인증을 위해 서명 검증용 공개 키 주소가 필요합니다.                              
현재 application.yaml의  provider.kakao  설정을 보면 이 부분이 누락되어 있습니다. 이 경우 토큰 발급/검증 단계에서 401 혹은 서명 오류가 발생할 수 있습니다.

• 해결책:  provider.kakao  하단에 아래 한 줄을 추가해 주어야 합니다.                                                                                                           
provider:                                                                                                                                                                     
kakao:                                                                                                                                                                      
authorization-uri: https://kauth.kakao.com/oauth/authorize?prompt=login                                                                                                   
token-uri: https://kauth.kakao.com/oauth/token                                                                                                                            
user-info-uri: https://kapi.kakao.com/v2/user/me                                                                                                                          
jwk-set-uri: https://kauth.kakao.com/.well-known/jwks.json # 👈 이 설정을 추가해야 OIDC 토큰 검증이 가능합니다.                                                           
user-name-attribute: sub # (OIDC 표준 식별 키인 sub 설정 권장)

──────
### 💡 3. 가장 확실하고 간편한 우회 방법 (OIDC 및 Secret 비활성화)

설정 정합성을 맞추는 과정이 계속 꼬인다면, 카카오 로그인을 가장 단순하고 보편적인 표준 OAuth 2.0 방식으로 전환하는 것을 추천합니다. (카카오는 구글과 다르게 Secret과 OIDC 없이도
매우 잘 작동합니다.)

1. 카카오 디벨로퍼스 콘솔 설정:                                                                                                                                                 
   •  내 애플리케이션 > 카카오 로그인 > OpenID Connect  ➔ **[OFF (사용 안 함)]**으로 설정 변경                                                                                
   •  내 애플리케이션 > 카카오 로그인 > 보안  ➔ Client Secret 활성화 상태를 **[사용 안 함 (OFF)]**으로 변경
2.  application.yml  설정 간소화:                                                                                                                                               
    •  client-secret ,  client-authentication-method , 그리고  openid  스코프를 모두 제거합니다.

    registration:                                                                                                                                                                 
      kakao:                                                                                                                                                                      
        client-id: 2c8fbb8aa00f8b6b76de558c86c8d013 # 실제 REST API 키                                                                                                            
        authorization-grant-type: authorization_code                                                                                                                              
        redirect-uri: "http://localhost:8080/login/oauth2/code/kakao"                                                                                                             
        client-name: Kakao                                                                                                                                                        
        scope:                                                                                                                                                                    
          - account_email                                                                                                                                                         
          - profile_nickname                                                                                                                                                      
    provider:                                                                                                                                                                     
      kakao:                                                                                                                                                                      
        authorization-uri: https://kauth.kakao.com/oauth/authorize?prompt=login                                                                                                   
        token-uri: https://kauth.kakao.com/oauth/token                                                                                                                            
        user-info-uri: https://kapi.kakao.com/v2/user/me                                                                                                                          
        user-name-attribute: id # 일반 OAuth 2.0일 때는 id를 그대로 씁니다.                                                                                                       


이 방식을 사용하면 복잡한 OIDC 서명 서티피케이트 검증과 Client Secret 인증 단계를 건너뛰기 때문에, 키 불일치로 인한 401 오류가 원천적으로 해결됩니다.

2026-06-09T17:19:07.711+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-1] o.s.security.web.FilterChainProxy        : Securing GET /oauth2/authorization/kakao
2026-06-09T17:19:07.772+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-1] o.s.s.web.DefaultRedirectStrategy        : Redirecting to https://kauth.kakao.com/oauth/authorize?prompt=login&response_type=code&client_id=2c8fbb8aa00f8b6b76de558c86c8d013&scope=openid%20account_email%20profile_nickname&state=C1SZGe-GV60WxStdiM1pVIWnFNpXGDs7X9HM_A_c3iw%3D&redirect_uri=http://localhost:8080/login/oauth2/code/kakao&nonce=MFbgYa1WHOzmdQlMA8Da-Vm40JasMfVtMZJdY0MN5hk
2026-06-09T17:19:32.920+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-2] o.s.security.web.FilterChainProxy        : Securing GET /login/oauth2/code/kakao?code=gVWhWYNkJPurfwUsECLt30j_wABRFVoDwoEwvrAGl61xNQ0m8QsKiQAAAAQKFwYuAAABnqt3JaLdCc_9be4aqQ&state=C1SZGe-GV60WxStdiM1pVIWnFNpXGDs7X9HM_A_c3iw%3D
2026-06-09T17:19:33.554+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-2] o.s.s.authentication.ProviderManager     : Authentication failed with provider OidcAuthorizationCodeAuthenticationProvider since [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: 401 : [no body]
2026-06-09T17:19:33.558+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-2] .s.a.DefaultAuthenticationEventPublisher : No event was found for the exception org.springframework.security.oauth2.core.OAuth2AuthenticationException
2026-06-09T17:19:33.558+09:00 DEBUG 17912 --- [ai-backend] [nio-8080-exec-2] o.s.s.authentication.ProviderManager     : Denying authentication since all attempted providers failed


provider property 에서 issur-uri 로 변경 후 작동함 ?

카카오 OAuth 로그인 설정에서  user-info-uri  등을 수동으로 지정했을 때 발생하던 오류가  issuer-uri 를 적용한 후 해결된 이유는 OpenID Connect(OIDC)의 메타데이터 자동            
검색(Discovery) 기능 덕분입니다.

이 현상이 발생하고 해결된 메커니즘을 카카오 공식 문서 및 Spring Security의 스펙을 기반으로 설명해 드립니다.                                                                     
──────
### 1. 원인:  scope: openid  설정으로 인한 OIDC 인증 활성화

작성하신 application.yaml을 보면  scope  설정에  openid 가 포함되어 있습니다.

    scope:                                                                                                                                                                        
      - openid                                                                                                                                                                    
      - account_email                                                                                                                                                             
      - profile_nickname                                                                                                                                                          

Spring Security OAuth2 Client는  scope 에  openid 가 포함되어 있으면 단순한 OAuth 2.0 흐름이 아닌 OIDC(OpenID Connect) 인증 흐름( OidcAuthorizationCodeAuthenticationProvider   
)으로 동작하게 됩니다.

OIDC 흐름에서는 카카오가 토큰 발급 시 사용자 정보가 담긴 **ID 토큰(ID Token)**을 함께 반환합니다. 클라이언트(스프링 서버)는 안전한 로그인을 위해 이 ID 토큰의 서명(Signature)이
위변조되지 않았는지 검증해야 합니다.

### 2. 문제 발생의 원인: 서명 검증용 공개키( jwk-set-uri )의 누락

ID 토큰을 검증하려면 카카오 인증 서버가 제공하는 공개키 목록이 있는 **JWK Set URI( jwk-set-uri )**가 필수적입니다.

• 이전 수동 설정 시  user-info-uri ,  token-uri  등만 지정하고  jwk-set-uri  설정을 누락하거나 잘못 지정했다면, 스프링 시큐리티는 수신한 ID 토큰의 서명을 검증할 방법이 없어   
인증을 실패 처리합니다.                                                                                                                                                         
• oauth에러메모.md의 179번 라인에서 발생한 아래의 오류 로그가 이를 보여줍니다.                                                                                                   
│  Authentication failed with provider OidcAuthorizationCodeAuthenticationProvider since [invalid_token_response] An error occurred while attempting to retrieve the OAuth 2.0  
│ Access Token Response: 401 : [no body]


### 3. 해결:  issuer-uri  적용 시 동작 원리 (OIDC Discovery)

OIDC 표준 명세(RFC 8414)에 따라 카카오 인증 서버는 설정 정보를 담은 메타데이터 문서(Discovery Document)를 제공하며, 해당 주소는 아래와 같습니다.

• 카카오 OIDC Discovery URL:  https://kauth.kakao.com/.well-known/openid-configuration

스프링 부트 설정에  issuer-uri: https://kauth.kakao.com 를 명시하면, 스프링 시큐리티는 서버가 실행될 때 이 디스커버리 주소로 요청을 보내 아래의 설정값들을 자동으로 확인하여    
구성합니다.

1.  authorization-uri  (인가 서버 경로)
2.  token-uri  (토큰 발급 경로)
3.  user-info-uri  (사용자 정보 조회 경로)
4.  jwk-set-uri  (ID 토큰 서명 검증용 공개키 목록 경로:  https://kauth.kakao.com/.well-known/jwks.json )
5.  user-name-attribute  (사용자 기본 식별값 Key: OIDC 표준인  sub )

즉,  user-info-uri  등을 일일이 지우고  issuer-uri  하나만 추가해 줌으로써, 스프링 시큐리티가 카카오 서버에서 서명 검증 키( jwk-set-uri )를 포함한 OIDC 필수 설정값들을 한 번에
안전하게 가져와 자동으로 구성했기 때문에 정상 작동하게 된 것입니다.                                                                                                             
──────
### 결론 및 권장사항

• OIDC 로그인( scope: openid )을 사용하시는 경우: 수동으로 여러 URI들을 기재하여 불일치 에러를 만드는 것보다, 지금처럼  issuer-uri 만 정의하여 설정을 자동화하는 것이 가장     
안전하고 표준화된 방법입니다.                                                                                                                                                   
• OIDC를 사용하지 않는 경우: 만약 카카오 디벨로퍼스 콘솔에서 OpenID Connect를 끄고 싶다면, application.yaml의  scope 에서  openid 를 제거하고 일반 OAuth 2.0 방식으로 전환하셔야
합니다. 

