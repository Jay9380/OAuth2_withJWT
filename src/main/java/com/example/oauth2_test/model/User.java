package com.example.oauth2_test.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
public class User {
    @Id//primaryKey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
    private int id;
//    @Column(name = "user_name")
    private String username;
//    @Column(name = "user_name")
    private String password;
    private String userImgUrl;
    private String email;
    private String role; //ROLE_USER, ROLE_ADMIN
    private String provider;
    private String providerId;
    @CreationTimestamp
    private Timestamp createDate;

    @Builder
    public User(String username, String userImgUrl,String email, String role, String provider, String providerId, Timestamp createDate) {
        this.username = username;
//        this.password = password;
        this.userImgUrl = userImgUrl;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createDate;
    }
}
