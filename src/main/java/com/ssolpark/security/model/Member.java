package com.ssolpark.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column
    private String name;

    @Column
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private Long kakaoId;

    @Column(updatable = false)
    @CreationTimestamp
    private Date updatedOn;

    @UpdateTimestamp
    private Date createdOn;

    @Builder
    public Member(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }
}
