package com.example.oauth2_test.config.auth;

//시큐리티가 /login 주소요청이 오면 낚아채서 로그인을 진행 시킨다.
//로그인을 진행이 완료가 되면 시큐리티 session을 만들어준다. (Security ContextHolder)
//오브젝트 = Authentication 타입 객체
//Authentication 안에 User정보가 있어야 함.
//User오브젝트의 타입 -> UserDtails타입 객체

//Security Session -> Authentication -> UserDetails(PrincipalDetails)    : (이렇게 정해져있음)

import com.example.oauth2_test.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//Security가  /login 주소 요청이 오면 낚아채서 로그인을 진행한다.
//로그인 진행이 완료되면 Security Session을 만들어준다. (Security가 자신만의 Session을 가진다)
//(Security ContextHolder)에 저장하며 여기에 들어갈수있는 오브젝트는 정해져있다\
//오브젝트 = Authentication 타입 객체
//Authentication안에 User정보가 들어있어야 함
//User 오브젝트 타입도 정해져있다 -> UserDetails타입 객체

//Security Session => Authentication => UserDetails(PrincipalDetails)

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
    private User user;//콤포지션
    private Map<String, Object> attributes;

    //일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    //OAuth  로그인, attributes 정보를 토대로 user객체를 만든다.
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    //해당 User의 권한을 리턴하는 곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //return 타입을 맞추기 위한 작업
        Collection<GrantedAuthority> collect = new ArrayList<>();

        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //계정 만료
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 1년 혹은 일정 기간이 지났는지.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //우리 사이트 1년동안 회원이 로그인을 안하면 휴먼계정

        //현재 시간들고 와서 1년을 초과하면 return false해주면 됨
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
