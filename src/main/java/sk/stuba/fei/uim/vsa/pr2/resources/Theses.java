/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import static java.lang.Math.log;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_ASSIGN_THEIS;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_DELETE_THEIS;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_GET;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_MAKE_THESIS;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_SUBMIT_THEIS;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.AsignScheme;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.CreateThesisRequest;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.Message;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.MyError;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.StudentAltResponse;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.StudentScheme;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.TeacherAltResponse;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.TeacherScheme;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.ThesisScheme;
import sk.stuba.fei.uim.vsa.pr2.solution.Student;
import sk.stuba.fei.uim.vsa.pr2.solution.Teacher;
import sk.stuba.fei.uim.vsa.pr2.solution.Thesis;
import sk.stuba.fei.uim.vsa.pr2.solution.ThesisService;

/**
 *
 * @author edu
 */
@Slf4j
@Path("theses")
@Provider
public class Theses {

  @Context
  SecurityContext securityContext;

  ThesisService ts = new ThesisService();

  @POST
  @Path("{id}/submit")
  @Secured({ PERM_TO_SUBMIT_THEIS })
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response submitThesis(
    @PathParam("id") Long id,
    AsignScheme asignScheme
  ) {
    Message message = new Message();
    try {
      log.info("before");
      Thesis thesis = ts.submitThesis(ts.getThesisByStudent(id).getId());
      log.info("after");

      if (thesis == null) throw new Exception("Internal server error.");

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = thesis.getSupervisor().getSupervisedTheses();
        for (Thesis thesisInside : allThesis) {
          allIds.add(thesisInside.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse.setId(thesis.getAuthor().getAisId());
      } catch (Exception e) {}
      try {
        studentAltResponse.setAisId(thesis.getAuthor().getAisId());
      } catch (Exception e) {}
      try {
        studentAltResponse.setName(thesis.getAuthor().getName());
      } catch (Exception e) {}
      try {
        studentAltResponse.setEmail(thesis.getAuthor().getEmail());
      } catch (Exception e) {}
      try {
        studentAltResponse.setYear(thesis.getAuthor().getYear());
      } catch (Exception e) {}
      try {
        studentAltResponse.setTerm(thesis.getAuthor().getTerm());
      } catch (Exception e) {}
      try {
        studentAltResponse.setProgramme(thesis.getAuthor().getStudyProgramme());
      } catch (Exception e) {}
      try {
        studentAltResponse.setThesis(thesis.getAuthor().getThesis().getId());
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getName(),
            thesis.getSupervisor().getEmail(),
            thesis.getSupervisor().getInstitute(),
            thesis.getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            thesis.getId(),
            thesis.getRegistrationNumber(),
            thesis.getTitle(),
            thesis.getDescription(),
            thesis.getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            thesis.getPublishedOn(),
            thesis.getDeadline(),
            thesis.getType(),
            thesis.getStatus()
          );
      } catch (Exception e) {}

      return Response
        .status(Response.Status.OK)
        .entity(tempThesisScheme)
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

  @DELETE
  @Path("{id}")
  @Secured({ PERM_TO_DELETE_THEIS })
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteThesis(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Thesis thesis = ts.deleteThesis(id);

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = thesis.getSupervisor().getSupervisedTheses();
        for (Thesis thesisInside : allThesis) {
          allIds.add(thesisInside.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getName(),
            thesis.getAuthor().getEmail(),
            thesis.getAuthor().getYear(),
            thesis.getAuthor().getTerm(),
            thesis.getAuthor().getStudyProgramme(),
            thesis.getAuthor().getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getName(),
            thesis.getSupervisor().getEmail(),
            thesis.getSupervisor().getInstitute(),
            thesis.getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            thesis.getId(),
            thesis.getRegistrationNumber(),
            thesis.getTitle(),
            thesis.getDescription(),
            thesis.getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            thesis.getPublishedOn(),
            thesis.getDeadline(),
            thesis.getType(),
            thesis.getStatus()
          );
      } catch (Exception e) {}

      return Response
        .status(Response.Status.OK)
        .entity(tempThesisScheme)
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

  @GET
  @Path("{id}")
  @Secured({ PERM_TO_GET })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getThesis(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Thesis thesis = ts.getThesis(id);

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = thesis.getSupervisor().getSupervisedTheses();
        for (Thesis thesisInside : allThesis) {
          allIds.add(thesisInside.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getName(),
            thesis.getAuthor().getEmail(),
            thesis.getAuthor().getYear(),
            thesis.getAuthor().getTerm(),
            thesis.getAuthor().getStudyProgramme(),
            thesis.getAuthor().getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getName(),
            thesis.getSupervisor().getEmail(),
            thesis.getSupervisor().getInstitute(),
            thesis.getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            thesis.getId(),
            thesis.getRegistrationNumber(),
            thesis.getTitle(),
            thesis.getDescription(),
            thesis.getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            thesis.getPublishedOn(),
            thesis.getDeadline(),
            thesis.getType(),
            thesis.getStatus()
          );
      } catch (Exception e) {}

      return Response
        .status(Response.Status.OK)
        .entity(tempThesisScheme)
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

  @GET
  @Secured({ PERM_TO_GET })
  @Produces(MediaType.APPLICATION_JSON)
  public Response getTheses() {
    Message message = new Message();
    try {
      List<Thesis> allThesis = ts.getTheses();

      List<ThesisScheme> allThesisScheme = new ArrayList<>();

      for (Thesis thesis : allThesis) {
        List<Long> allIds = new ArrayList<>();
        try {
          List<Thesis> allThesisInside = thesis
            .getSupervisor()
            .getSupervisedTheses();
          for (Thesis thesisInside : allThesisInside) {
            allIds.add(thesisInside.getId());
          }
        } catch (Exception e) {}

        StudentAltResponse studentAltResponse = new StudentAltResponse();

        try {
          studentAltResponse =
            new StudentAltResponse(
              thesis.getAuthor().getAisId(),
              thesis.getAuthor().getAisId(),
              thesis.getAuthor().getName(),
              thesis.getAuthor().getEmail(),
              thesis.getAuthor().getYear(),
              thesis.getAuthor().getTerm(),
              thesis.getAuthor().getStudyProgramme(),
              thesis.getAuthor().getThesis().getId()
            );
        } catch (Exception e) {}

        TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

        try {
          teacherAltResponse =
            new TeacherAltResponse(
              thesis.getSupervisor().getAisId(),
              thesis.getSupervisor().getAisId(),
              thesis.getSupervisor().getName(),
              thesis.getSupervisor().getEmail(),
              thesis.getSupervisor().getInstitute(),
              thesis.getSupervisor().getDepartment(),
              allIds
            );
        } catch (Exception e) {}

        ThesisScheme tempThesisScheme = new ThesisScheme();

        try {
          tempThesisScheme =
            new ThesisScheme(
              thesis.getId(),
              thesis.getRegistrationNumber(),
              thesis.getTitle(),
              thesis.getDescription(),
              thesis.getDepartment(),
              teacherAltResponse,
              studentAltResponse,
              thesis.getPublishedOn(),
              thesis.getDeadline(),
              thesis.getType(),
              thesis.getStatus()
            );
        } catch (Exception e) {}

        allThesisScheme.add(tempThesisScheme);
      }

      return Response
        .status(Response.Status.OK)
        .entity(allThesisScheme.toArray(new ThesisScheme[0]))
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
  @Secured({ PERM_TO_MAKE_THESIS })
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response makeThesisAssignment(
    CreateThesisRequest createThesisRequest
  ) {
    Message message = new Message();
    try {
      Thesis thesis = ts.makeThesisAssignment(
        createThesisRequest.getRegistrationNumber(),
        createThesisRequest.getTitle(),
        createThesisRequest.getDescription(),
        createThesisRequest.getType()
      );
      if (thesis == null) throw new Exception("Internal server error.");

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = thesis.getSupervisor().getSupervisedTheses();
        for (Thesis thesisInside : allThesis) {
          allIds.add(thesisInside.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse =
          new StudentAltResponse(
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getAisId(),
            thesis.getAuthor().getName(),
            thesis.getAuthor().getEmail(),
            thesis.getAuthor().getYear(),
            thesis.getAuthor().getTerm(),
            thesis.getAuthor().getStudyProgramme(),
            thesis.getAuthor().getThesis().getId()
          );
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getName(),
            thesis.getSupervisor().getEmail(),
            thesis.getSupervisor().getInstitute(),
            thesis.getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            thesis.getId(),
            thesis.getRegistrationNumber(),
            thesis.getTitle(),
            thesis.getDescription(),
            thesis.getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            thesis.getPublishedOn(),
            thesis.getDeadline(),
            thesis.getType(),
            thesis.getStatus()
          );
      } catch (Exception e) {}

      return Response
        .status(Response.Status.CREATED)
        .entity(tempThesisScheme)
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

  //assignThesis(Long thesisId, Long studentId)
  @POST
  @Path("{id}/assign")
  @Secured({ PERM_TO_ASSIGN_THEIS })
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response assignThesis(
    @PathParam("id") Long id,
    AsignScheme asignScheme
  ) {
    Message message = new Message();
    try {
      Thesis thesis = ts.assignThesis(id, asignScheme.getStudentId());

      if (thesis == null) throw new Exception("Internal server error.");

      List<Long> allIds = new ArrayList<>();
      try {
        List<Thesis> allThesis = thesis.getSupervisor().getSupervisedTheses();
        for (Thesis thesisInside : allThesis) {
          allIds.add(thesisInside.getId());
        }
      } catch (Exception e) {}

      StudentAltResponse studentAltResponse = new StudentAltResponse();

      try {
        studentAltResponse.setId(thesis.getAuthor().getAisId());
      } catch (Exception e) {}
      try {
        studentAltResponse.setAisId(thesis.getAuthor().getAisId());
      } catch (Exception e) {}
      try {
        studentAltResponse.setName(thesis.getAuthor().getName());
      } catch (Exception e) {}
      try {
        studentAltResponse.setEmail(thesis.getAuthor().getEmail());
      } catch (Exception e) {}
      try {
        studentAltResponse.setYear(thesis.getAuthor().getYear());
      } catch (Exception e) {}
      try {
        studentAltResponse.setTerm(thesis.getAuthor().getTerm());
      } catch (Exception e) {}
      try {
        studentAltResponse.setProgramme(thesis.getAuthor().getStudyProgramme());
      } catch (Exception e) {}
      try {
        studentAltResponse.setThesis(thesis.getAuthor().getThesis().getId());
      } catch (Exception e) {}

      TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

      try {
        teacherAltResponse =
          new TeacherAltResponse(
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getAisId(),
            thesis.getSupervisor().getName(),
            thesis.getSupervisor().getEmail(),
            thesis.getSupervisor().getInstitute(),
            thesis.getSupervisor().getDepartment(),
            allIds
          );
      } catch (Exception e) {}

      ThesisScheme tempThesisScheme = new ThesisScheme();

      try {
        tempThesisScheme =
          new ThesisScheme(
            thesis.getId(),
            thesis.getRegistrationNumber(),
            thesis.getTitle(),
            thesis.getDescription(),
            thesis.getDepartment(),
            teacherAltResponse,
            studentAltResponse,
            thesis.getPublishedOn(),
            thesis.getDeadline(),
            thesis.getType(),
            thesis.getStatus()
          );
      } catch (Exception e) {}

      return Response
        .status(Response.Status.OK)
        .entity(tempThesisScheme)
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
}
