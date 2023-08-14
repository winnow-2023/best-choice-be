package com.winnow.bestchoice.domain;

import com.winnow.bestchoice.type.MemberStatus;
import com.winnow.bestchoice.type.Provider;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, name = "nickname")
  private String nickname;

  @Column(nullable = false, unique = true, name = "email")
  private String email;

  @Column(nullable = false, name = "status")
  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  @Column(nullable = false, name = "provider")
  @Enumerated(EnumType.STRING)
  private Provider provider;

  @Column(nullable = false, unique = true, name = "refresh_token")
  private String refreshToken;

  @CreatedDate
  @Column(nullable = false, name = "created_date")
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false, name = "modified_date")
  private LocalDateTime modifiedDate;

}
