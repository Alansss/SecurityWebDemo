package com.starv.task.entity;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity(name = "user")
@NoArgsConstructor
public class UserInfo implements Serializable, UserDetails {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer id;

      @Column
      private String username;

      @Column
      private String password;

      @Transient
      private String role;

      @Transient
      private boolean accountNonExpired;

      @Transient
      private boolean accountNonLocked;

      @Transient
      private boolean credentialsNonExpired;

      @Transient
      private boolean enabled;

      public UserInfo(String username, String password, String role, boolean accountNonExpired, boolean accountNonLocked,
                  boolean credentialsNonExpired, boolean enabled) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.accountNonExpired = accountNonExpired;
            this.accountNonLocked = accountNonLocked;
            this.credentialsNonExpired = credentialsNonExpired;
            this.enabled = enabled;
      }

      // 这是权限
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
      }
      @Override
      public String getPassword() {
            return password;
      }
      @Override
      public String getUsername() {
            return username;
      }
      @Override
      public boolean isAccountNonExpired() {
            return accountNonExpired;
      }
      @Override
      public boolean isAccountNonLocked() {
            return accountNonLocked;
      }
      @Override
      public boolean isCredentialsNonExpired() {
            return credentialsNonExpired;
      }
      @Override
      public boolean isEnabled() {
            return enabled;
      }
}