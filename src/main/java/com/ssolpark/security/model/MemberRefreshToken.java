package com.ssolpark.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table
public class MemberRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberRefreshTokenId;

    @Column(unique = true)
    private String refreshToken;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "FK_member_refresh_token"))
    private Member member;

    private Date expiredOn;

    @UpdateTimestamp
    private Date updatedOn;

    @CreationTimestamp
    private Date createdOn;

    @Builder
    public MemberRefreshToken(String refreshToken, Member member, Date expiredOn) {
        this.refreshToken = refreshToken;
        this.member = member;
        this.expiredOn = expiredOn;
    }

    public void updateRefreshTokenAndExpiredOn(String refreshToken, Date expiredOn) {
        this.refreshToken = refreshToken;
        this.expiredOn = expiredOn;
    }
}
