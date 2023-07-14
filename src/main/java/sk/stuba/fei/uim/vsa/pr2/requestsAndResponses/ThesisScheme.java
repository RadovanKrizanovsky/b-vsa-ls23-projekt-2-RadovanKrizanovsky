/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisStatus;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisType;

/**
 *
 * @author edu
 */

@Data
public class ThesisScheme {

  private Long id;
  private String registrationNumber;
  private String title;
  private String description;
  private String department;
  private TeacherAltResponse supervisor;
  private StudentAltResponse author;
  private Date publishedOn;
  private Date deadline;
  private ThesisType type;
  private ThesisStatus status;

  public ThesisScheme() {}

  public ThesisScheme(
    Long id,
    String registrationNumber,
    String title,
    String description,
    String department,
    TeacherAltResponse supervisor,
    StudentAltResponse author,
    Date publishedOn,
    Date deadline,
    ThesisType type,
    ThesisStatus status
  ) {
    this.id = id;
    this.registrationNumber = registrationNumber;
    this.title = title;
    this.description = description;
    this.department = department;
    this.supervisor = supervisor;
    this.author = author;
    this.publishedOn = publishedOn;
    this.deadline = deadline;
    this.type = type;
    this.status = status;
  }
}
