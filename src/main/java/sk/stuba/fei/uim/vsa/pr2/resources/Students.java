/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_DELETE_STUDENT;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_GET;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_POST;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.Message;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.MyError;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.StudentAltResponse;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.StudentScheme;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.TeacherAltResponse;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.ThesisScheme;
import sk.stuba.fei.uim.vsa.pr2.solution.Student;
import sk.stuba.fei.uim.vsa.pr2.solution.Thesis;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisService;

/**
 *
 * @author edu
 */
@Slf4j
@Path("students")
@Provider
public class Students {

  @Context
  SecurityContext securityContext;

  ThesisService ts = new ThesisService();

  @DELETE
  @Path("{id}")
  @Secured({ PERM_TO_DELETE_STUDENT })
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteStudent(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Student student = ts.deleteStudent(id);

      StudentScheme stdentScheme = new StudentScheme();
      stdentScheme.setId(student.getAisId());
      stdentScheme.setAisId(student.getAisId());
      stdentScheme.setName(student.getName());
      stdentScheme.setEmail(student.getEmail());
      stdentScheme.setYear(student.getYear());
      stdentScheme.setTerm(student.getTerm());
      stdentScheme.setProgramme(student.getStudyProgramme());

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = student
          .getThesis()
          .getSupervisor()
          .getSupervisedTheses();
        for (Thesis thesis : allThesis) {
          allIds.add(thesis.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            student.getAisId(),
            student.getAisId(),
            student.getName(),
            student.getEmail(),
            student.getYear(),
            student.getTerm(),
            student.getStudyProgramme(),
            student.getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getName(),
            student.getThesis().getSupervisor().getEmail(),
            student.getThesis().getSupervisor().getInstitute(),
            student.getThesis().getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            student.getThesis().getId(),
            student.getThesis().getRegistrationNumber(),
            student.getThesis().getTitle(),
            student.getThesis().getDescription(),
            student.getThesis().getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            student.getThesis().getPublishedOn(),
            student.getThesis().getDeadline(),
            student.getThesis().getType(),
            student.getThesis().getStatus()
          );
      } catch (Exception e) {}

      stdentScheme.setThesis(tempThesisScheme);

      return Response.status(Response.Status.OK).entity(stdentScheme).build();
    } catch (Exception e) {
      log.info(String.valueOf(e));

      message.setCode(500);
      message.setMessage(e.getMessage());
      MyError error = new MyError();
      error.setTrace(String.valueOf(e.getStackTrace()));
      error.setType(String.valueOf(e.getCause()));
      message.setError(error);
    }
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(message)
      .build();
  }

  @GET
  @Path("{id}")
  @Secured({ PERM_TO_GET })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStudent(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Student student = ts.getStudent(id);

      StudentScheme stdentScheme = new StudentScheme();
      stdentScheme.setId(student.getAisId());
      stdentScheme.setAisId(student.getAisId());
      stdentScheme.setName(student.getName());
      stdentScheme.setEmail(student.getEmail());
      stdentScheme.setYear(student.getYear());
      stdentScheme.setTerm(student.getTerm());
      stdentScheme.setProgramme(student.getStudyProgramme());

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = student
          .getThesis()
          .getSupervisor()
          .getSupervisedTheses();
        for (Thesis thesis : allThesis) {
          allIds.add(thesis.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            student.getAisId(),
            student.getAisId(),
            student.getName(),
            student.getEmail(),
            student.getYear(),
            student.getTerm(),
            student.getStudyProgramme(),
            student.getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getName(),
            student.getThesis().getSupervisor().getEmail(),
            student.getThesis().getSupervisor().getInstitute(),
            student.getThesis().getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            student.getThesis().getId(),
            student.getThesis().getRegistrationNumber(),
            student.getThesis().getTitle(),
            student.getThesis().getDescription(),
            student.getThesis().getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            student.getThesis().getPublishedOn(),
            student.getThesis().getDeadline(),
            student.getThesis().getType(),
            student.getThesis().getStatus()
          );
      } catch (Exception e) {}

      stdentScheme.setThesis(tempThesisScheme);

      return Response.status(Response.Status.OK).entity(stdentScheme).build();
    } catch (Exception e) {
      log.info(String.valueOf(e));

      message.setCode(500);
      message.setMessage(e.getMessage());
      MyError error = new MyError();
      error.setTrace(String.valueOf(e.getStackTrace()));
      error.setType(String.valueOf(e.getCause()));
      message.setError(error);
    }
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(message)
      .build();
  }

  @GET
  @Secured({ PERM_TO_GET })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStudents() {
    Message message = new Message();
    try {
      List<Student> allStudents = ts.getStudents();
      List<StudentScheme> allStudentsScheme = new ArrayList<>();

      for (Student student : allStudents) {
        StudentScheme stdentScheme = new StudentScheme();
        stdentScheme.setId(student.getAisId());
        stdentScheme.setAisId(student.getAisId());
        stdentScheme.setName(student.getName());
        stdentScheme.setEmail(student.getEmail());
        stdentScheme.setYear(student.getYear());
        stdentScheme.setTerm(student.getTerm());
        stdentScheme.setProgramme(student.getStudyProgramme());

        List<Long> allIds = new ArrayList<>();
        try {
          List<Thesis> allThesis = student
            .getThesis()
            .getSupervisor()
            .getSupervisedTheses();
          for (Thesis thesis : allThesis) {
            allIds.add(thesis.getId());
          }
        } catch (Exception e) {}

        StudentAltResponse studentAltResponse = new StudentAltResponse();

        try {
          studentAltResponse =
            new StudentAltResponse(
              student.getAisId(),
              student.getAisId(),
              student.getName(),
              student.getEmail(),
              student.getYear(),
              student.getTerm(),
              student.getStudyProgramme(),
              student.getThesis().getId()
            );
        } catch (Exception e) {}

        TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

        try {
          teacherAltResponse =
            new TeacherAltResponse(
              student.getThesis().getSupervisor().getAisId(),
              student.getThesis().getSupervisor().getAisId(),
              student.getThesis().getSupervisor().getName(),
              student.getThesis().getSupervisor().getEmail(),
              student.getThesis().getSupervisor().getInstitute(),
              student.getThesis().getSupervisor().getDepartment(),
              allIds
            );
        } catch (Exception e) {}

        ThesisScheme tempThesisScheme = new ThesisScheme();

        try {
          tempThesisScheme =
            new ThesisScheme(
              student.getThesis().getId(),
              student.getThesis().getRegistrationNumber(),
              student.getThesis().getTitle(),
              student.getThesis().getDescription(),
              student.getThesis().getDepartment(),
              teacherAltResponse,
              studentAltResponse,
              student.getThesis().getPublishedOn(),
              student.getThesis().getDeadline(),
              student.getThesis().getType(),
              student.getThesis().getStatus()
            );
        } catch (Exception e) {}

        stdentScheme.setThesis(tempThesisScheme);

        allStudentsScheme.add(stdentScheme);
      }

      return Response
        .status(Response.Status.OK)
        .entity(allStudentsScheme.toArray(new StudentScheme[0]))
        .build();
    } catch (Exception e) {
      log.info(String.valueOf(e));

      message.setCode(500);
      message.setMessage(e.getMessage());
      MyError error = new MyError();
      error.setTrace(String.valueOf(e.getStackTrace()));
      error.setType(String.valueOf(e.getCause()));
      message.setError(error);
    }
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(message)
      .build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStudent(CreateStudentRequest createStudentRequest) {
    Message message = new Message();
    try {
      Student student = ts.createStudent(
        createStudentRequest.getAisId(),
        createStudentRequest.getName(),
        createStudentRequest.getEmail(),
        BCryptService.hash(createStudentRequest.getPassword()),
        createStudentRequest.getYear(),
        createStudentRequest.getTerm(),
        createStudentRequest.getProgramme()
      );
      if (student == null) throw new Exception("Internal server error.");

      StudentScheme stdentScheme = new StudentScheme();
      stdentScheme.setId(student.getAisId());
      stdentScheme.setAisId(student.getAisId());
      stdentScheme.setName(student.getName());
      stdentScheme.setEmail(student.getEmail());
      stdentScheme.setYear(student.getYear());
      stdentScheme.setTerm(student.getTerm());
      stdentScheme.setProgramme(student.getStudyProgramme());

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = student
          .getThesis()
          .getSupervisor()
          .getSupervisedTheses();
        for (Thesis thesis : allThesis) {
          allIds.add(thesis.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            student.getAisId(),
            student.getAisId(),
            student.getName(),
            student.getEmail(),
            student.getYear(),
            student.getTerm(),
            student.getStudyProgramme(),
            student.getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getAisId(),
            student.getThesis().getSupervisor().getName(),
            student.getThesis().getSupervisor().getEmail(),
            student.getThesis().getSupervisor().getInstitute(),
            student.getThesis().getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            student.getThesis().getId(),
            student.getThesis().getRegistrationNumber(),
            student.getThesis().getTitle(),
            student.getThesis().getDescription(),
            student.getThesis().getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            student.getThesis().getPublishedOn(),
            student.getThesis().getDeadline(),
            student.getThesis().getType(),
            student.getThesis().getStatus()
          );
      } catch (Exception e) {}

      stdentScheme.setThesis(tempThesisScheme);

      return Response
        .status(Response.Status.CREATED)
        .entity(stdentScheme)
        .build();
    } catch (Exception e) {
      log.info(String.valueOf(e));

      message.setCode(500);
      message.setMessage(e.getMessage());
      MyError error = new MyError();
      error.setTrace(String.valueOf(e.getStackTrace()));
      error.setType(String.valueOf(e.getCause()));
      message.setError(error);
    }
    return Response
      .status(Response.Status.INTERNAL_SERVER_ERROR)
      .entity(message)
      .build();
  }

  private String[] extractFromAuthHeader(String authHeader) {
    return new String(
      Base64.getDecoder().decode(authHeader.replace("Basic", "").trim())
    )
      .split(":");
  }
}
