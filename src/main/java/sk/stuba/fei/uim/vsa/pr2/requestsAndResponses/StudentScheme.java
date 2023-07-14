/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.solution.Student;
import sk.stuba.fei.uim.vsa.pr2.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisStatus;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisType;

/**
 *
 * @author edu
 */
@Data
@AllArgsConstructor
public class StudentScheme {

  private Long id;
  private Long aisId;
  private String name;
  private String email;
  private Integer year;
  private Integer term;
  private String programme;
  private ThesisScheme thesis;

  public StudentScheme() {}

  public void setThesis(ThesisScheme thesis) {
    this.thesis = thesis;
  }
}
