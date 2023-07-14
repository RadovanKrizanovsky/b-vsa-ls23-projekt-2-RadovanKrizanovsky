/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.requestsAndResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisType;

/**
 *
 * @author edu
 */

@Data
@AllArgsConstructor
public class CreateThesisRequest {

  private String registrationNumber;
  private String title;
  private String description;
  private ThesisType type;

  public CreateThesisRequest() {}
}
