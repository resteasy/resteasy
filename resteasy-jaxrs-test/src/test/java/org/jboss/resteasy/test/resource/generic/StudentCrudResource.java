package org.jboss.resteasy.test.resource.generic;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

/**
 * RESTEasy should be able to use type parameter values (Student, Integer) for (de)marshalling parameters/entity body.
 *
 * @author Jozef Hartinger
 */
@Path("/student")
@Produces("application/student")
@Consumes("application/student")
public class StudentCrudResource extends CrudResource<Student, Integer>
{

   private static Map<Integer, Student> students = new HashMap<Integer, Student>();

   public StudentCrudResource()
   {
      students.put(1, new Student("Jozef Hartinger"));
   }

   @Override
   Student getEntity(Integer id)
   {
      return students.get(id);
   }

   @Override
   void setEntity(Integer id, Student entity)
   {
      students.put(id, entity);
   }
}
