package com.winnow.bestchoice.domain;

import com.winnow.bestchoice.type.MemberStatus;
import com.winnow.bestchoice.type.AuthProvider;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Member implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, name = "nickname")
  private String nickname;

  @Column(nullable = false, unique = true, name = "email")
  private String email;

  @Column(nullable = false, name = "status")
  @Enumerated(EnumType.STRING)
  private MemberStatus status = MemberStatus.ACTIVE;

  @Column(nullable = false, name = "provider")
  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

//  @Column(nullable = false, unique = true, name = "refresh_token")
//  private String refreshToken;

  @CreatedDate
  @Column(nullable = false, name = "created_date")
  private LocalDateTime createdDate;

  @LastModifiedDate
  @Column(nullable = false, name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("user"));
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
