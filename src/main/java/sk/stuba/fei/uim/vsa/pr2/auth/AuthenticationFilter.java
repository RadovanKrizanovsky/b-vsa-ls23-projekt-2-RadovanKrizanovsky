package sk.stuba.fei.uim.vsa.pr2.auth;

import static java.lang.Long.parseLong;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.solution.Student;
import sk.stuba.fei.uim.vsa.pr2.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisService;
import sk.stuba.fei.uim.vsa.pr2.users.User;

@Slf4j
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  @Context
  private ResourceInfo resourceInfo;

  ThesisService ts = new ThesisService();

  @Override
  public void filter(ContainerRequestContext request) throws IOException {
    String authHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
    String[] credentials = extractFromAuthHeader(authHeader);
    final SecurityContext securityContext = request.getSecurityContext();

    Method resourceMethod = resourceInfo.getResourceMethod();
    Set<Permission> permissions = extractPermissionFromMethod(resourceMethod);

    Optional<Student> student = ts.getStudentByHisEmail(credentials[0]);
    Optional<Teacher> teacher = ts.getTeacherByHisEmail(credentials[0]);
    if (student.isPresent()) {
      if (!BCryptService.verify(credentials[1], student.get().getPassword())) {
        request.abortWith(
          Response.status(Response.Status.UNAUTHORIZED).build()
        );
        return;
      }

      User user = new User(
        student.get().getEmail(),
        student.get().getPassword(),
        0
      );
      user.addPermission(Permission.PERM_TO_GET);
      user.addPermission(Permission.PERM_TO_POST);
      user.addPermission(Permission.PERM_TO_SUBMIT_THEIS);
      user.addPermission(Permission.PERM_TO_ASSIGN_THEIS);

      if ("deleteStudent".equals(resourceInfo.getResourceMethod().getName())) {
        Optional<String> idd = Optional.ofNullable(
          request.getUriInfo().getPathParameters().getFirst("id")
        );
        if (student.get().getAisId() == parseLong(idd.get())) {
          user.addPermission(Permission.PERM_TO_DELETE_STUDENT);
        }
      }

      BasicSecurityContext context = new BasicSecurityContext(user);
      context.setSecure(securityContext.isSecure());
      request.setSecurityContext(context);
      return;
    } else if (teacher.isPresent()) {
      if (!BCryptService.verify(credentials[1], teacher.get().getPassword())) {
        request.abortWith(
          Response.status(Response.Status.UNAUTHORIZED).build()
        );
        return;
      }

      User user = new User(
        teacher.get().getEmail(),
        teacher.get().getPassword(),
        1
      );

      user.addPermission(Permission.PERM_TO_GET);
      user.addPermission(Permission.PERM_TO_POST);
      user.addPermission(Permission.PERM_TO_DELETE_STUDENT);
      user.addPermission(Permission.PERM_TO_MAKE_THESIS);
      user.addPermission(Permission.PERM_TO_DELETE_THEIS);
      user.addPermission(Permission.PERM_TO_SUBMIT_THEIS);
      user.addPermission(Permission.PERM_TO_ASSIGN_THEIS);

      if ("deleteTeacher".equals(resourceInfo.getResourceMethod().getName())) {
        Optional<String> idd = Optional.ofNullable(
          request.getUriInfo().getPathParameters().getFirst("id")
        );
        if (teacher.get().getAisId() == parseLong(idd.get())) {
          user.addPermission(Permission.PERM_TO_DELETE_TEACHER);
        }
      }

      BasicSecurityContext context = new BasicSecurityContext(user);
      context.setSecure(securityContext.isSecure());
      request.setSecurityContext(context);

      return;
    } else {
      request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      return;
    }
  }

  private Set<Permission> extractPermissionFromMethod(Method method) {
    if (method == null) {
      return new HashSet<>();
    }
    Secured secured = method.getAnnotation(Secured.class);
    if (secured == null) {
      return new HashSet<>();
    }
    return new HashSet<>(Arrays.asList(secured.value()));
  }

  private String[] extractFromAuthHeader(String authHeader) {
    return new String(
      Base64.getDecoder().decode(authHeader.replace("Basic", "").trim())
    )
      .split(":");
  }
}
