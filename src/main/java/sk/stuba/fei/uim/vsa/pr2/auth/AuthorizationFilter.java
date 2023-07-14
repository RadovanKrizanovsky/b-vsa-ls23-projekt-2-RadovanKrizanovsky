/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.auth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
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
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr2.users.User;

/**
 *
 * @author edu
 */

@Slf4j
@Provider
@Secured
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

  @Context
  private ResourceInfo resourceInfo;

  @Override
  public void filter(ContainerRequestContext request) throws IOException {
    User user = (User) request.getSecurityContext().getUserPrincipal();

    Method resourceMethod = resourceInfo.getResourceMethod();
    Set<Permission> permissions = extractPermissionFromMethod(resourceMethod);

    //sttudent maze sameho seba
    /*
        String authHeader = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        String[] credentials = extractFromAuthHeader(authHeader);
        
        //addPermission(Permission.PERM_TO_DELETE_STUDENT)
        
                    
        if(student.get().getEmail().equals(credentials[0])){
            user.addPermission(Permission.PERM_TO_DELETE_STUDENT);
        }
            
        
        // if
        log.info("HERE-------------------------");
        log.info(resourceMethod.getName());
        */

    if (user.getPermissions().stream().noneMatch(permissions::contains)) {
      request.abortWith(Response.status(Response.Status.FORBIDDEN).build());
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
