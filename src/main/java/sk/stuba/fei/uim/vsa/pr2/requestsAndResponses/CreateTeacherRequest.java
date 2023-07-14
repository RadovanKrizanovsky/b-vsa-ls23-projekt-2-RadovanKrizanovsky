/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.solution.Thesis;

/**
 *
 * @author edu
 */

@Data
@AllArgsConstructor
public class CreateTeacherRequest {

  private Long aisId;
  private String name;
  private String email;
  private String password;
  private String institute;
  private String department;

  //private List<Thesis> supervisedTheses;

  public CreateTeacherRequest() {}
}
