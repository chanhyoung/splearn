package tobyspring.splearn.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {
  Member member;
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    this.passwordEncoder = new PasswordEncoder() {
      @Override
      public String encode(String password) {
        return password.toLowerCase();
      }

      @Override
      public boolean matches(String password, String passwordHash) {
        return encode(password).equals(passwordHash);
      }
    };

    member = Member.create("toby@splearn.app", "toby", "secret", passwordEncoder);
  }

  @Test
  void createMember() {
    Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
  }

  @Test
  void activate() {
    member.activate();
    
    Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
  }

  @Test
  void activateTwice() {
    member.activate();
    
    Assertions.assertThatThrownBy(() -> member.activate())
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void deactivate() {
    member.activate();

    member.deactivate();

    Assertions.assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
  }

  @Test
  void deactivateFail() {
    Assertions.assertThatThrownBy(() -> member.deactivate()).isInstanceOf(IllegalStateException.class);

    member.activate();
    member.deactivate();

    Assertions.assertThatThrownBy(() -> member.deactivate()).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void verifyPassword() {
    Assertions.assertThat(member.verifyPassword("secret", passwordEncoder)).isTrue();
    Assertions.assertThat(member.verifyPassword("wrong", passwordEncoder)).isFalse();  
  }

  @Test
  void changeNickname() {
    Assertions.assertThat(member.getNickname()).isEqualTo("toby");
    
    member.changeNickname("Charlie");

    Assertions.assertThat(member.getNickname()).isEqualTo("Charlie"); 
  }
  
  @Test
  void changePassword() {
    member.changePassword("newsecret", passwordEncoder);

    Assertions.assertThat(member.verifyPassword("newsecret", passwordEncoder)).isTrue();
  }

}
