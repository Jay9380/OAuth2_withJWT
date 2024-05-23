package com.example.oauth2_test.config;


import com.example.oauth2_test.config.oauth.PrincipleOauth2UserService;
import com.example.oauth2_test.handler.PrincipalSuccessHandler;
import com.example.oauth2_test.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;


/*
				1. 코드를 받기(인증)
				2. 엑세스 토큰(사용자 정보에 접근가능한 권한)
				3. 사용자 프로필 정보를 가져옴
				4. 그 정보를 토대로 회원가입을 진행 시킴
				-> 그 외의 정보 (집주소, 회원등급 등등)가 필요하게 된다면 추가적으로 정보를 더 받는다
				만약 없다면 구글이 주는 기본정보로 로그인을 시키면 된다.
 */



@Configuration
@EnableWebSecurity //시큐리티 활성화 -> 기본 스프링 필터 체인에 등록 -> SecurityConfig 이것을 말함.
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
//prePostEnabled = true 의 경우 preAuthorize, postAuthorize 두가지의 어노테이션을 활성화 시켜준다.
public class SecurityConfig  {

    @Autowired
    private PrincipleOauth2UserService principleOauth2UserService;

    @Autowired
    private PrincipalSuccessHandler principalSuccessHandler;

    @Autowired
    private JWTUtil jwtUtil;


    //해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트의 주소
                        configuration.setAllowedMethods(Collections.singletonList("*"));//모든 요청 허용
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        // 인가(접근권한) 설정
        http.authorizeHttpRequests().requestMatchers("/user/**").authenticated() //user라는 url로 들어오면 authenticated 인증 필요
                .requestMatchers("/manager/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER") //manager로 들어온다면 해당 권한이 있어야함
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN") //admin으로 들어온다면 해당 권한이 있어야함
                .anyRequest().permitAll(); //그외의 요청은 모두 허용한다.

        http.formLogin().loginPage("/loginForm")
                .loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행 -> Controller에 /login 안만들어도됨
                .defaultSuccessUrl("/") //로그인이 완료되면 /기본 메인 페이지로 이동, 혹은 어떤 특정 페이지에서 loginForm을 요청하여 로그인하면 해당 페이지로 보내준다.
                .and()
                .oauth2Login() //oauth2로그인이 시작되면
                .loginPage("/loginForm") //구글 로그인이 완료 된 뒤의 후처리가 필요함, 구글 로그인이 되면 엑세스 토큰  + 사용자 프로필 정보를 함께 받음음
                .userInfoEndpoint()
                .userService(principleOauth2UserService); //Service내부에 들어가는 것은 Oauth2UserService가 되어야한다.


        //                .usernameParameter("ㅇㅇ") //PrincipalDetailsService 에서와 String 일치 하지 않으면 여기서 설정


        //csrf disable - 사이트 위변조 방지
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //JWT 토큰을 이용하기 때문에 session을 사용하지 않기 때문에 STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        // 로그인 설정
//        http.formLogin()
//                .loginPage("/user2/login")
//                .defaultSuccessUrl("/user2/loginSuccess")
//                .failureUrl("/user2/login?success=100)")
//                .usernameParameter("uid")
//                .passwordParameter("pass");

//        // 로그아웃 설정
//        http.logout()
//                .invalidateHttpSession(true)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login?success=200");

        // 사용자 인증 처리 컴포넌트 서비스 등록
//        http.userDetailsService(service);

        return http.build();
    }
}
