package org.jboss.resteasy.test.validation;

import java.util.Iterator;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.validation.Foo;
import org.jboss.resteasy.validation.FooConstraint;
import org.jboss.resteasy.validation.FooReaderWriter;
import org.jboss.resteasy.validation.FooValidator;
import org.jboss.resteasy.validation.JaxRsActivator;
import org.jboss.resteasy.validation.TestClassConstraint;
import org.jboss.resteasy.validation.TestClassValidator;
import org.jboss.resteasy.validation.TestResourceWithAllViolationTypes;
import org.jboss.resteasy.validation.TestResourceWithReturnValues;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 16, 2012
 */
@RunWith(Arquillian.class)
public class TestValidation
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(Foo.class, FooConstraint.class, FooReaderWriter.class, FooValidator.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addClasses(TestResourceWithAllViolationTypes.class, TestResourceWithReturnValues.class)
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testReturnValues() throws Exception
   {
      // Valid native constraint
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/native");
      Foo foo = new Foo("a");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);     
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());
      
      // Valid imposed constraint
      request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/imposed");
      foo = new Foo("abcde");
      request.body("application/foo", foo);
      response = request.post(Foo.class);      
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Valid native and imposed constraints.
      request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/nativeAndImposed");
      foo = new Foo("abc");
      request.body("application/foo", foo);
      response = request.post(Foo.class);      
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      {
         // Invalid native constraint
         request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/native");
         request.body("application/foo", new Foo("abcdef"));
         response = request.post();
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         String entity = response.getEntity(String.class);
         System.out.println("entity: " + entity);
         ResteasyViolationException e = new ResteasyViolationException(entity);
         ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
         System.out.println("violation: " + violation);
         Assert.assertTrue(violation.getMessage().equals("s must have length: 1 <= length <= 3"));
         Assert.assertEquals("Foo[abcdef]", violation.getValue());
      }
      
      {
         // Invalid imposed constraint
         request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/imposed");
         request.body("application/foo", new Foo("abcdef"));
         response = request.post(Foo.class);
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         String entity = response.getEntity(String.class);
         System.out.println("entity: " + entity);
         ResteasyViolationException e = new ResteasyViolationException(entity);
         countViolations(e, 1, 0, 0, 0, 0, 1);
         ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
         System.out.println("violation: " + violation);
         Assert.assertTrue(violation.getMessage().equals("s must have length: 3 <= length <= 5"));
         Assert.assertEquals("Foo[abcdef]", violation.getValue());
      }
      
      {
         // Invalid native and imposed constraints
         request = new ClientRequest("http://localhost:8080/Validation-test/rest/return/nativeAndImposed");
         request.body("application/foo", new Foo("abcdef"));
         response = request.post(Foo.class); 
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         String entity = response.getEntity(String.class);
         System.out.println("entity: " + entity);
         ResteasyViolationException e = new ResteasyViolationException(entity);
         countViolations(e, 2, 0, 0, 0, 0, 2);
         Iterator<ResteasyConstraintViolation > it = e.getReturnValueViolations().iterator(); 
         ResteasyConstraintViolation cv1 = it.next();
         ResteasyConstraintViolation cv2 = it.next();
         if (cv1.getMessage().indexOf('1') < 0)
         {
            ResteasyConstraintViolation temp = cv1;
            cv1 = cv2;
            cv2 = temp;
         }
         Assert.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"));
         Assert.assertEquals("Foo[abcdef]", cv1.getValue());
         Assert.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"));
         Assert.assertEquals("Foo[abcdef]", cv2.getValue());
      }
   }

   @Test
   public void testViolationsBeforeReturnValue() throws Exception
   {
      // Valid
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/all/abc/wxyz");
      Foo foo = new Foo("pqrs");
      request.body("application/foo", foo);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals(foo, response.getEntity());

      // Invalid: Should have 1 each of field, property, class, and parameter violations,
      //          and no return value violations.
      request = new ClientRequest("http://localhost:8080/Validation-test/rest/all/a/z");
      foo = new Foo("p");
      request.body("application/foo", foo);
      response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      String entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
      ResteasyViolationException e = new ResteasyViolationException(entity);
      countViolations(e, 4, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation violation = e.getFieldViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("size must be between 2 and 4", violation.getMessage());
      Assert.assertEquals("a", violation.getValue());
      violation = e.getPropertyViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("size must be between 3 and 5", violation.getMessage());
      Assert.assertEquals("z", violation.getValue());
      violation = e.getClassViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("Concatenation of s and t must have length > 5", violation.getMessage());
      System.out.println("violation value: " + violation.getValue());
      Assert.assertTrue(violation.getValue().startsWith("org.jboss.resteasy.validation.TestResourceWithAllViolationTypes@"));
      violation = e.getParameterViolations().iterator().next();
      System.out.println("violation: " + violation);
      Assert.assertEquals("s must have length: 3 <= length <= 5", violation.getMessage());
      Assert.assertEquals("Foo[p]", violation.getValue());
   }
   
   private void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
