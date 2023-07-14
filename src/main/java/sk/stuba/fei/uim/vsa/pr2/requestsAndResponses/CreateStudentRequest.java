/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author edu
 */

@Data
@AllArgsConstructor
public class CreateStudentRequest {

  private Long aisId;
  private String name;
  private String email;
  private String password;
  private Integer year;
  private Integer term;
  private String programme;

  public CreateStudentRequest() {}
}
