package com.example.oauth2_test.controller;

import com.example.oauth2_test.config.auth.PrincipalDetails;
import com.example.oauth2_test.model.User;
import com.example.oauth2_test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //View를 리턴하겠다.
public class IndexController {

    @Autowired
    private UserRepository userRepository; //UserRepository는 JPA에 의해 Bean으로 등록되어있어서 @Autowired로 가져와서 쓴다.

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            //@AuthenticationPrincipal를 통해서 session 정보 접근이 가능, 여기서는 @AuthenticationPrincipal이 UserDetails타입을 가지고 있다.
            //하지만 UserDetails는 PrincipalDetails를 implements  했기 때문에 타입을 PrincipalDetails로 받을 수 있다.
            //@AuthenticationPrincipal UserDetails userDetails){
            @AuthenticationPrincipal PrincipalDetails userDetails){
        /*
            DI의존성을 주입한 뒤
            authentication 내부에 getPrincipal()가 존재하는데 return 타입이 Object이기 때문에
            principalDetails을 다운캐스팅하여 getUser()를 호출한다면 PrincipalDetails자체를 확인이 가능하다.
         */
        System.out.println("/test/login ==================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails : "+ principalDetails.getUser());
        //principalDetails : User(id=7, username=test, password=$2a$10$m/eYj1q3i0F/CHqltezgyuAoMv9cIhUP/LSU3OrHueUSJnH9hAFfy, email=test@test.com, role=ROLE_USER, createDate=2024-05-11 01:10:49.0)


        System.out.println("authentication : " + authentication.getPrincipal());
        //authentication.getPrincipal() => com.example.oauth2_test.config.auth.PrincipalDetails@79a1f15c
        //return 타입이 Object이다.

//        @AuthenticationPrincipal PrincipalDetails userDetails){로 변경됨에 따라
        System.out.println("userDetails : "+ userDetails.getUsername());
        System.out.println("userDetails : "+ userDetails.getUser()); //.getUser()가 가능해진다.
        return "세션 정보 확인하기";
    }


    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth){

        System.out.println("/test/oauth/login ==================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("principalDetails : "+ oAuth2User.getAttributes());
        //principalDetails : {sub=101665371092364897267, name=제이철, given_name=철, family_name=제이, picture=https://lh3.googleusercontent.com/a/ACg8ocI-Dxjeepm58vwBAHzC9r3LgzjF1u85jDNtj53L4_r-hx9kzHw=s96-c, email=parkjuncheol77@gmail.com, email_verified=true, locale=ko}

        //oAuth2User.getAttributes()와 동일한 결과.
        //User 정보에 있어서 authentication으로 접근이 가능하고, @AuthenticationPrincipal 어노테이션으로도 접근이 가능하다. 어노테이션에서는 타입이 OAuth2User타입니다.
        System.out.println("oauth2User : "+ oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"","/"})
    public String index(){
        //기본폴더 src/main/resources/
        //뷰 리졸버 설정: templates (prefix), mustache(sufix)
        return "index"; //여기서 index는 view가 된다.
        //src/main/resources/templates/index.mustache를찾게 되어있다.
    }

    //OAuth 로그인을 해도 PrincipalDetails로 받을 수 있음
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails : "+ principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        System.out.println("호출");
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    //스프링시큐리티 해당주소를 낚아채버린다. -> SecurityConfig 파일 생성 후 작동안함.
    @PostMapping("/join") //joinForm에서 Post방식으로 보냄
    public String join(User user) {

        //패스워드가 암호화 되지 않으면 Security에 의해 가입이 안됨
        String rawPassword = user.getPassword(); //패스워드를 불러와
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); //패스워드 encoding을 진행한다.

        System.out.println("user ===>" + user);
        user.setRole("ROLE_USER");

        user.setPassword(encPassword);

        userRepository.save(user);

        return "redirect:/loginForm"; //redirect를 붙이면 /loginForm이 붙어있는 함수를 호출한다.
    }

    @Secured("ROLE_ADMIN") //@EnableMethodSecurity에 의해 동작, 권한이  ADMIN일때 접속 가능
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    //두개의 제한을 걸고 싶다면 PreAuthorize를 활용, prePostEnabled = true에 의해동작
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }


//
//    @GetMapping("/joinProc")
//    public @ResponseBody String joinProc() {
//        return "회원가입 완료 됨";
//    }
}
