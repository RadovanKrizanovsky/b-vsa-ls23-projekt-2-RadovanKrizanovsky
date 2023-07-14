/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.stuba.fei.uim.vsa.pr2.resources;

import static java.lang.Math.log;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_DELETE_TEACHER;
import static sk.stuba.fei.uim.vsa.pr2.auth.Permission.PERM_TO_GET;

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
import sk.stuba.fei.uim.vsa.pr2.BCryptService;
import sk.stuba.fei.uim.vsa.pr2.auth.Secured;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.CreateStudentRequest;
import sk.stuba.fei.uim.vsa.pr2.requestsAndResponses.CreateTeacherRequest;
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
@Path("teachers")
@Provider
public class Teachers {

  @Context
  SecurityContext securityContext;

  ThesisService ts = new ThesisService();

  @DELETE
  @Path("{id}")
  @Secured({ PERM_TO_DELETE_TEACHER })
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteTeacher(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Teacher teacher = ts.deleteTeacher(id);

      TeacherScheme teacherScheme = new TeacherScheme();

      teacherScheme.setId(teacher.getAisId());
      teacherScheme.setAisId(teacher.getAisId());
      teacherScheme.setName(teacher.getName());
      teacherScheme.setEmail(teacher.getEmail());
      teacherScheme.setInstitute(teacher.getInstitute());
      teacherScheme.setDepartment(teacher.getDepartment());

      List<Thesis> allTheses = new ArrayList<>();
      List<ThesisScheme> allThesesSchemes = new ArrayList<>();

      try {
        allTheses = teacher.getSupervisedTheses();
      } catch (Exception e) {}

      try {
        for (Thesis thesis : allTheses) {
          ThesisScheme thesisScheme = new ThesisScheme();
          thesisScheme.setId(thesis.getId());
          thesisScheme.setRegistrationNumber(thesis.getRegistrationNumber());
          thesisScheme.setTitle(thesis.getTitle());
          thesisScheme.setDescription(thesis.getDescription());
          thesisScheme.setDepartment(thesis.getDepartment());

          TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

          List<Long> allIds = new ArrayList<>();
          try {
            List<Thesis> allThesis = teacher.getSupervisedTheses();
            for (Thesis thesisInside : allThesis) {
              allIds.add(thesisInside.getId());
            }
          } catch (Exception e) {}

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
          thesisScheme.setSupervisor(teacherAltResponse);

          StudentAltResponse studentAltResponse = new StudentAltResponse();

          try {} catch (Exception e) {
            studentAltResponse.setId(thesis.getAuthor().getAisId());
            studentAltResponse.setAisId(thesis.getAuthor().getAisId());
            studentAltResponse.setName(thesis.getAuthor().getName());
            studentAltResponse.setEmail(thesis.getAuthor().getEmail());
            studentAltResponse.setYear(thesis.getAuthor().getYear());
            studentAltResponse.setTerm(thesis.getAuthor().getTerm());
            studentAltResponse.setProgramme(
              thesis.getAuthor().getStudyProgramme()
            );
            studentAltResponse.setThesis(
              thesis.getAuthor().getThesis().getId()
            );
          }

          thesisScheme.setAuthor(studentAltResponse);

          try {
            thesisScheme.setPublishedOn(thesis.getPublishedOn());
            thesisScheme.setDeadline(thesis.getDeadline());
            thesisScheme.setType(thesis.getType());
            thesisScheme.setStatus(thesis.getStatus());
          } catch (Exception e) {}

          allThesesSchemes.add(thesisScheme);
        }
      } catch (Exception e) {}

      teacherScheme.setTheses(allThesesSchemes);

      /*
            List<Long> allIds = new ArrayList<>();
            try {
                List<Thesis> allThesis = student.getThesis().getSupervisor().getSupervisedTheses();
                for (Thesis thesis : allThesis) {
                    allIds.add(thesis.getId());
                }
            } catch (Exception e) {
                
            }
            
            StudentAltResponse studentAltResponse = new StudentAltResponse();
            
            try {
                        studentAltResponse = new StudentAltResponse(
                        student.getAisId(),
                        student.getAisId(),
                        student.getName(),
                        student.getEmail(),
                        student.getYear(),
                        student.getTerm(),
                        student.getStudyProgramme(),
                        student.getThesis().getId());
            } catch (Exception e) {
            }
            
            TeacherAltResponse teacherAltResponse = new TeacherAltResponse();
            
            try {
                    teacherAltResponse = new TeacherAltResponse(
                    student.getThesis().getSupervisor().getAisId(),
                    student.getThesis().getSupervisor().getAisId(),
                    student.getThesis().getSupervisor().getName(),
                    student.getThesis().getSupervisor().getEmail(),
                    student.getThesis().getSupervisor().getPassword(),
                    student.getThesis().getSupervisor().getInstitute(),
                    student.getThesis().getSupervisor().getDepartment(),
                    allIds);
            } catch (Exception e) {
            }

            
            ThesisScheme tempThesisScheme = new ThesisScheme();
            
            
            try {
                        tempThesisScheme = new ThesisScheme(
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
                    student.getThesis().getStatus());
            } catch (Exception e) {
            }

            
            stdentScheme.setThesis(tempThesisScheme);
            */

      return Response.status(Response.Status.OK).entity(teacherScheme).build();
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
  public Response getTeacher(
    @PathParam("id") Long id
    /*@Context SecurityContext securityContext*/
  ) {
    //log.info(securityContext.getUserPrincipal().toString());
    //log.info(((User)securityContext.getUserPrincipal()).getName());

    Message message = new Message();
    try {
      Teacher teacher = ts.getTeacher(id);

      TeacherScheme teacherScheme = new TeacherScheme();

      teacherScheme.setId(teacher.getAisId());
      teacherScheme.setAisId(teacher.getAisId());
      teacherScheme.setName(teacher.getName());
      teacherScheme.setEmail(teacher.getEmail());
      teacherScheme.setInstitute(teacher.getInstitute());
      teacherScheme.setDepartment(teacher.getDepartment());

      List<Thesis> allTheses = new ArrayList<>();
      List<ThesisScheme> allThesesSchemes = new ArrayList<>();

      try {
        allTheses = teacher.getSupervisedTheses();
      } catch (Exception e) {}

      try {
        for (Thesis thesis : allTheses) {
          ThesisScheme thesisScheme = new ThesisScheme();
          thesisScheme.setId(thesis.getId());
          thesisScheme.setRegistrationNumber(thesis.getRegistrationNumber());
          thesisScheme.setTitle(thesis.getTitle());
          thesisScheme.setDescription(thesis.getDescription());
          thesisScheme.setDepartment(thesis.getDepartment());

          TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

          List<Long> allIds = new ArrayList<>();
          try {
            List<Thesis> allThesis = teacher.getSupervisedTheses();
            for (Thesis thesisInside : allThesis) {
              allIds.add(thesisInside.getId());
            }
          } catch (Exception e) {}

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
          thesisScheme.setSupervisor(teacherAltResponse);

          StudentAltResponse studentAltResponse = new StudentAltResponse();

          try {} catch (Exception e) {
            studentAltResponse.setId(thesis.getAuthor().getAisId());
            studentAltResponse.setAisId(thesis.getAuthor().getAisId());
            studentAltResponse.setName(thesis.getAuthor().getName());
            studentAltResponse.setEmail(thesis.getAuthor().getEmail());
            studentAltResponse.setYear(thesis.getAuthor().getYear());
            studentAltResponse.setTerm(thesis.getAuthor().getTerm());
            studentAltResponse.setProgramme(
              thesis.getAuthor().getStudyProgramme()
            );
            studentAltResponse.setThesis(
              thesis.getAuthor().getThesis().getId()
            );
          }

          thesisScheme.setAuthor(studentAltResponse);

          try {
            thesisScheme.setPublishedOn(thesis.getPublishedOn());
            thesisScheme.setDeadline(thesis.getDeadline());
            thesisScheme.setType(thesis.getType());
            thesisScheme.setStatus(thesis.getStatus());
          } catch (Exception e) {}

          allThesesSchemes.add(thesisScheme);
        }
      } catch (Exception e) {}

      teacherScheme.setTheses(allThesesSchemes);

      return Response.status(Response.Status.OK).entity(teacherScheme).build();
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
  public Response getTeachers() {
    Message message = new Message();
    try {
      List<Teacher> allTeachers = ts.getTeachers();

      List<TeacherScheme> allTeacherScheme = new ArrayList<>();

      for (Teacher teacher : allTeachers) {
        TeacherScheme teacherScheme = new TeacherScheme();

        teacherScheme.setId(teacher.getAisId());
        teacherScheme.setAisId(teacher.getAisId());
        teacherScheme.setName(teacher.getName());
        teacherScheme.setEmail(teacher.getEmail());
        teacherScheme.setInstitute(teacher.getInstitute());
        teacherScheme.setDepartment(teacher.getDepartment());

        List<Thesis> allTheses = new ArrayList<>();
        List<ThesisScheme> allThesesSchemes = new ArrayList<>();

        try {
          allTheses = teacher.getSupervisedTheses();
        } catch (Exception e) {}

        try {
          for (Thesis thesis : allTheses) {
            ThesisScheme thesisScheme = new ThesisScheme();
            thesisScheme.setId(thesis.getId());
            thesisScheme.setRegistrationNumber(thesis.getRegistrationNumber());
            thesisScheme.setTitle(thesis.getTitle());
            thesisScheme.setDescription(thesis.getDescription());
            thesisScheme.setDepartment(thesis.getDepartment());

            TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

            List<Long> allIds = new ArrayList<>();
            try {
              List<Thesis> allThesis = teacher.getSupervisedTheses();
              for (Thesis thesisInside : allThesis) {
                allIds.add(thesisInside.getId());
              }
            } catch (Exception e) {}

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
            thesisScheme.setSupervisor(teacherAltResponse);

            StudentAltResponse studentAltResponse = new StudentAltResponse();

            try {} catch (Exception e) {
              studentAltResponse.setId(thesis.getAuthor().getAisId());
              studentAltResponse.setAisId(thesis.getAuthor().getAisId());
              studentAltResponse.setName(thesis.getAuthor().getName());
              studentAltResponse.setEmail(thesis.getAuthor().getEmail());
              studentAltResponse.setYear(thesis.getAuthor().getYear());
              studentAltResponse.setTerm(thesis.getAuthor().getTerm());
              studentAltResponse.setProgramme(
                thesis.getAuthor().getStudyProgramme()
              );
              studentAltResponse.setThesis(
                thesis.getAuthor().getThesis().getId()
              );
            }

            thesisScheme.setAuthor(studentAltResponse);

            try {
              thesisScheme.setPublishedOn(thesis.getPublishedOn());
              thesisScheme.setDeadline(thesis.getDeadline());
              thesisScheme.setType(thesis.getType());
              thesisScheme.setStatus(thesis.getStatus());
            } catch (Exception e) {}

            allThesesSchemes.add(thesisScheme);
          }
        } catch (Exception e) {}

        teacherScheme.setTheses(allThesesSchemes);

        allTeacherScheme.add(teacherScheme);
      }

      return Response
        .status(Response.Status.OK)
        .entity(allTeacherScheme.toArray(new TeacherScheme[0]))
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
  public Response createTeacher(CreateTeacherRequest createTeacherRequest) {
    Message message = new Message();
    try {
      Teacher teacher = ts.createTeacher(
        createTeacherRequest.getAisId(),
        createTeacherRequest.getName(),
        createTeacherRequest.getEmail(),
        BCryptService.hash(createTeacherRequest.getPassword()),
        createTeacherRequest.getInstitute(),
        createTeacherRequest.getDepartment()
      );
      if (teacher == null) throw new Exception("Internal server error.");

      TeacherScheme teacherScheme = new TeacherScheme();

      teacherScheme.setId(teacher.getAisId());
      teacherScheme.setAisId(teacher.getAisId());
      teacherScheme.setName(teacher.getName());
      teacherScheme.setEmail(teacher.getEmail());
      teacherScheme.setInstitute(teacher.getInstitute());
      teacherScheme.setDepartment(teacher.getDepartment());

      List<Thesis> allTheses = new ArrayList<>();
      List<ThesisScheme> allThesesSchemes = new ArrayList<>();

      try {
        allTheses = teacher.getSupervisedTheses();
      } catch (Exception e) {}

      try {
        for (Thesis thesis : allTheses) {
          ThesisScheme thesisScheme = new ThesisScheme();
          thesisScheme.setId(thesis.getId());
          thesisScheme.setRegistrationNumber(thesis.getRegistrationNumber());
          thesisScheme.setTitle(thesis.getTitle());
          thesisScheme.setDescription(thesis.getDescription());
          thesisScheme.setDepartment(thesis.getDepartment());

          TeacherAltResponse teacherAltResponse = new TeacherAltResponse();

          List<Long> allIds = new ArrayList<>();
          try {
            List<Thesis> allThesis = teacher.getSupervisedTheses();
            for (Thesis thesisInside : allThesis) {
              allIds.add(thesisInside.getId());
            }
          } catch (Exception e) {}

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
          thesisScheme.setSupervisor(teacherAltResponse);

          StudentAltResponse studentAltResponse = new StudentAltResponse();

          try {} catch (Exception e) {
            studentAltResponse.setId(thesis.getAuthor().getAisId());
            studentAltResponse.setAisId(thesis.getAuthor().getAisId());
            studentAltResponse.setName(thesis.getAuthor().getName());
            studentAltResponse.setEmail(thesis.getAuthor().getEmail());
            studentAltResponse.setYear(thesis.getAuthor().getYear());
            studentAltResponse.setTerm(thesis.getAuthor().getTerm());
            studentAltResponse.setProgramme(
              thesis.getAuthor().getStudyProgramme()
            );
            studentAltResponse.setThesis(
              thesis.getAuthor().getThesis().getId()
            );
          }

          thesisScheme.setAuthor(studentAltResponse);

          try {
            thesisScheme.setPublishedOn(thesis.getPublishedOn());
            thesisScheme.setDeadline(thesis.getDeadline());
            thesisScheme.setType(thesis.getType());
            thesisScheme.setStatus(thesis.getStatus());
          } catch (Exception e) {}

          allThesesSchemes.add(thesisScheme);
        }
      } catch (Exception e) {}

      teacherScheme.setTheses(allThesesSchemes);

      return Response
        .status(Response.Status.CREATED)
        .entity(teacherScheme)
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
