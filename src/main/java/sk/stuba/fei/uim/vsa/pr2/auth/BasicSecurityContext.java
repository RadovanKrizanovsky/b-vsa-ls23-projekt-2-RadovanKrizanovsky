package sk.stuba.fei.uim.vsa.pr2.auth;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import sk.stuba.fei.uim.vsa.pr2.users.User;

public class BasicSecurityContext implements SecurityContext {

  private User user;
  private boolean secure;

  public BasicSecurityContext(User user) {
    this.user = user;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  @Override
  public Principal getUserPrincipal() {
    return user;
  }

  @Override
  public boolean isUserInRole(String s) {
    return false;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  @Override
  public String getAuthenticationScheme() {
    return "Basic";
  }
}
