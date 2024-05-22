package com.example.oauth2_test.config.auth;

import com.example.oauth2_test.model.User;
import com.example.oauth2_test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//시큐리티 설정에서 loginProcessingUrl("/login");'
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어있는 loadUserByUsername 함수가 실행 됨 (규칙)
@Service
public class PrincipalDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    //Security Session => Authentication => UserDetails(PrincipalDetails)
    //시큐리티 session(내부 Authentication(내부 UserDetails))
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //String 변수명을 맞춰줘야함 (html파일과)

        System.out.println("PrincipalDetailsService : username ===>"+ username);
        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            System.out.println("Null이 아닌 경우");
            System.out.println("userEntity==>" + userEntity);
            return new PrincipalDetails(userEntity);
        }
        System.out.println("Null인경우");
        return null;
    }
}
