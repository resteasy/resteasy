package org.jboss.resteasy.test.resource.patch;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/students")
public class StudentResource
{
   private static Map<Long, Student> studentsMap = new java.util.concurrent.ConcurrentHashMap<Long, Student>();

   public StudentResource()
   {

   }

   @GET
   @Path("/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Student getStudent(@PathParam("id") long id)
   {
      Student student = studentsMap.get(id);
      if (student == null)
      {
         throw new NotFoundException();
      }
      return student;
   }

   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Student addStudent(Student student)
   {
      studentsMap.put(student.getId(), student);
      return student;
   }

   @PUT
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Student updateStudent(@PathParam("id") long id, Student student)
   {
      if (studentsMap.get(id) == null)
      {
         throw new NotFoundException();
      }
      studentsMap.put(id, student);
      return student;
   }

   @PATCH
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Student patchStudent(@PathParam("id") long id, Student student)
   {
      if (studentsMap.get(id) == null)
      {
         throw new NotFoundException();
      }
      studentsMap.put(id, student);
      return student;
   }

}
