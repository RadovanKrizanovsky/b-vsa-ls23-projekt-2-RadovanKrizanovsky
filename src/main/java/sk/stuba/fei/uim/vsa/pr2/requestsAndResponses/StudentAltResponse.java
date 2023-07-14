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
public class StudentAltResponse {

  private Long id;
  private Long aisId;
  private String name;
  private String email;
  private Integer year;
  private Integer term;
  private String programme;
  private Long thesis;

  public StudentAltResponse() {}

  public StudentAltResponse(
    Long id,
    Long aisId,
    String name,
    String email,
    Integer year,
    Integer term,
    String programme,
    Long thesis
  ) {
    this.id = id;
    this.aisId = aisId;
    this.name = name;
    this.email = email;
    this.year = year;
    this.term = term;
    this.programme = programme;
    this.thesis = thesis;
  }
}
