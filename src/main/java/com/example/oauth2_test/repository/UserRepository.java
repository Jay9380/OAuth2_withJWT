package com.example.oauth2_test.repository;

import com.example.oauth2_test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//JPA Repository가 CRUD 함수를 들고 있음
//@Repository라는 어노테이션이 없어도 ioC가능. 이유는 JPARepositorty를 상속 했기 때문에
public interface UserRepository extends JpaRepository<User, Integer> { //User이고 Primary Key 는 Integer이다.
    //findBy 규칙 ->Username문법
    //select * from user where username =?
    User findByUsername(String username);

    //select * from user where email = ?
//    public User findByUserEmail();
}
