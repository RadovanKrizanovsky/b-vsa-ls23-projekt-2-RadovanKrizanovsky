/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author edu
 */

@Data
public class TeacherAltResponse {

  private Long id;
  private Long aisId;
  private String name;
  private String email;
  private String institute;
  private String department;
  private List<Long> supervisedTheses;

  public TeacherAltResponse() {}

  public TeacherAltResponse(
    Long id,
    Long aisId,
    String name,
    String email,
    String institute,
    String department,
    List<Long> supervisedTheses
  ) {
    this.id = id;
    this.aisId = aisId;
    this.name = name;
    this.email = email;
    this.institute = institute;
    this.department = department;
    this.supervisedTheses = supervisedTheses;
  }
}
