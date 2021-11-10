package org.jboss.resteasy.test.resource.patch;

import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/students")
public class StudentResource
{
   private static Map<Long, Student> studentsMap = new java.util.concurrent.ConcurrentHashMap<Long, Student>();

   public StudentResource()
   {

   }

   @GET
   @Path("/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
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
   @PATCH
   @Path("/{id}")
   @Consumes("application/merge-patch+json")
   @Produces(MediaType.APPLICATION_JSON)
   public Student mergePatchStudent(@PathParam("id") long id, Student student)
   {
      if (studentsMap.get(id) == null)
      {
         throw new NotFoundException();
      }
      studentsMap.put(id, student);
      return student;
   }

}
