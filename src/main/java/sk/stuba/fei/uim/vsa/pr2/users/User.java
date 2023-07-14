package sk.stuba.fei.uim.vsa.pr2.users;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;

@Data
public class User implements Principal {

  private String email;
  private String password;
  private Integer role;
  private List<Permission> permissions;

  public User() {
    permissions = new ArrayList<>();
  }

  public User(
    String email,
    String password,
    Integer role,
    List<Permission> permissions
  ) {
    this.email = email;
    this.password = password;
    this.role = role;
    this.permissions = permissions;
  }

  public User(String email, String password, Integer role) {
    this();
    this.email = email;
    this.password = password;
    this.role = role;
  }

  @Override
  public String getName() {
    return this.email;
  }

  public void addPermission(Permission permission) {
    this.permissions.add(permission);
  }
}
