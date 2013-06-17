package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.fail;

import java.io.Serializable;

import javax.validation.ValidationException;
import javax.validation.constraints.Size;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

/**
 * 
 * A TestValidateOnExecution.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 8, 2013
 */
public class TestValidateOnExecution
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   public static void before(Class<?> resourceClass) throws Exception
   {
      after();
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(resourceClass);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecution()
   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public interface TestValidationOnExecuteInterface
   {
   }

   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public interface TestValidationOnExecuteSubInterface
   {
   }

   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionResource implements TestValidationOnExecuteSubInterface
   {
      @POST
      @Path("none")
      @Size(min = 1)
      public String none(@Size(max = 1) String s)
      {
         return s;
      }

      @POST
      @Path("getterOnNonGetter")
      @Size(min = 1)
      @ValidateOnExecution(type = { ExecutableType.GETTER_METHODS, ExecutableType.CONSTRUCTORS, ExecutableType.NONE })
      public String nongetter1(@Size(max = 1) String s)
      {
         return s;
      }

      @POST
      @Path("nonGetterOnGetter")
      @Size(min = 1)
      @ValidateOnExecution(type = { ExecutableType.NON_GETTER_METHODS, ExecutableType.CONSTRUCTORS, ExecutableType.NONE })
      public String getS1()
      {
         return "abc";
      }

      @POST
      @Path("implicitOnNonGetter")
      @Size(min = 1)
      @ValidateOnExecution(type = { ExecutableType.IMPLICIT })
      public String nongetter2(@Size(max = 1) String s)
      {
         return s;
      }

      @POST
      @Path("implicitOnGetter")
      @Size(max = 1)
      @ValidateOnExecution(type = { ExecutableType.IMPLICIT })
      // Will be validated when other methods are called, returning a property
      // violation.
      public String getS2()
      {
         return "abc";
      }

      @POST
      @Path("allOnNonGetter")
      @Size(min = 1)
      @ValidateOnExecution(type = { ExecutableType.ALL })
      public String nongetter3(@Size(max = 1) String s)
      {
         return s;
      }

      @POST
      @Path("allOnGetter")
      @Size(max = 1)
      @ValidateOnExecution(type = { ExecutableType.ALL })
      // Will be validated when other methods are called, returning a property
      // violation.
      public String getS3()
      {
         return "abc";
      }

      @POST
      @Path("override")
      @Size(min = 1)
      @ValidateOnExecution(type = { ExecutableType.ALL })
      public String override(@Size(max = 1) String s)
      {
         return s;
      }
   }

   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionSubResource extends TestValidateOnExecutionResource
   {
      @POST
      @Path("override")
      @Size(min = 1)
      public String override(@Size(max = 1) String s)
      {
         return s;
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidClassOneLevel()
   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionErrorOneLevel_Class extends TestValidateOnExecutionResource
   {
      @POST
      @Path("override")
      @Size(min = 1)
      @Override
      @ValidateOnExecution(type = { ExecutableType.IMPLICIT })
      public String override(@Size(max = 1) String s)
      {
         return s;
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidTwoLevels_Class()
   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionErrorTwoLevels_Class extends TestValidateOnExecutionSubResource
   {
      @POST
      @Path("override")
      @Size(min = 1)
      @Override
      @ValidateOnExecution(type = { ExecutableType.IMPLICIT })
      public String override(@Size(max = 1) String s)
      {
         return s;
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidOneLevel_Interface()
   public interface TestValidateOnExecutionInterface
   {
      @POST
      @Path("overrideInterface1")
      @ValidateOnExecution(type = { ExecutableType.NONE })
      public void overrideInterface1(String s);
   }

   public interface TestValidateOnExecutionSubInterface extends
         TestValidateOnExecutionInterface
   {
      @POST
      @Path("overrideInterface2")
      @ValidateOnExecution(type = { ExecutableType.NONE })
      public void overrideInterface2(String s);
   }

   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionErrorOneLevel_Interface implements TestValidateOnExecutionSubInterface
   {
      @POST
      @Path("overrideInterface1")
      @Override
      public void overrideInterface1(String s)
      {
      }

      @POST
      @Path("overrideInterface2")
      @ValidateOnExecution(type = { ExecutableType.ALL })
      @Override
      public void overrideInterface2(String s)
      {
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidTwoLevels_Interface()
   @Path("")
   @ValidateOnExecution(type = { ExecutableType.NONE })
   public static class TestValidateOnExecutionErrorTwoLevels_Interface implements TestValidateOnExecutionSubInterface
   {
      @POST
      @Path("overrideInterface1")
      @ValidateOnExecution(type = { ExecutableType.ALL })
      @Override
      public void overrideInterface1(String s)
      {
      }

      @POST
      @Path("overrideInterface2")
      @Override
      public void overrideInterface2(String s)
      {
      }
   }
   
   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidGenericOverride_Class()
   @Path("")
   public static class TestValidateOnExecutionErrorWithGenericMethodSuperClass<T>
   {
      @POST
      @Path("bar")
      @ValidateOnExecution(type = { ExecutableType.ALL })
      public void override(T qux)
      {
      }
   }
   
   @Path("")
   public static class TestValidateOnExecutionErrorWithGenericMethodSubClass extends TestValidateOnExecutionErrorWithGenericMethodSuperClass<String>
   {
      @POST
      @Path("bar")
      @ValidateOnExecution(type = { ExecutableType.ALL })
      @Override
      public void override(String qux)
      {
      }
   }
   
   //////////////////////////////////////////////////////////////////////////
   // testValidateOnExecutionInvalidGenericOverride_Interface()
   @Path("")
   public interface InterfaceWithGenericSupermethod<T>
   {
      @POST
      @Path("bar")
      @ValidateOnExecution(type = { ExecutableType.NONE })
      public void override(T qux);
   }

   @Path("")
   public static class TestValidateOnExecutionErrorWithGenericSupermethod implements InterfaceWithGenericSupermethod<String>
   {
      @POST
      @Path("bar")
      @ValidateOnExecution(type = { ExecutableType.ALL })
      @Override
      public void override(String qux)
      {
      }
   }
   
   //////////////////////////////////////////////////////////////////////////
   @Test
   // @Ignore
   public void testValidateOnExecution() throws Exception
   {
      before(TestValidateOnExecutionSubResource.class);

      {
         // No method validation. Two property violations.
         ClientRequest request = new ClientRequest(generateURL("/none"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         System.out.println(e);
         countViolations(e, 2, 0, 2, 0, 0, 0);
      }

      {
         // No method validation. Two property violations.
         ClientRequest request = new ClientRequest(
               generateURL("/getterOnNonGetter"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         System.out.println(e);
         countViolations(e, 2, 0, 2, 0, 0, 0);
      }

      {
         // No method validation. Two property violations
         ClientRequest request = new ClientRequest(
               generateURL("/nonGetterOnGetter"));
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         System.out.println(e);
         countViolations(e, 2, 0, 2, 0, 0, 0);
      }

      {
         // Failure.
         ClientRequest request = new ClientRequest(
               generateURL("/implicitOnNonGetter"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         System.out.println(e);
         countViolations(e, 3, 0, 2, 0, 1, 0);
      }

      {
         // Failure.
         ClientRequest request = new ClientRequest(
               generateURL("/implicitOnGetter"));
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         countViolations(e, 2, 0, 2, 0, 0, 0);
      }

      {
         // Failure.
         ClientRequest request = new ClientRequest(
               generateURL("/allOnNonGetter"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         countViolations(e, 3, 0, 2, 0, 1, 0);
      }

      {
         // Failure.
         ClientRequest request = new ClientRequest(generateURL("/allOnGetter"));
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         countViolations(e, 2, 0, 2, 0, 0, 0);
      }

      {
         // Failure.
         ClientRequest request = new ClientRequest(generateURL("/override"));
         request.body(MediaType.TEXT_PLAIN_TYPE, "abc");
         ClientResponse<?> response = request.post(Serializable.class);
         Assert.assertEquals(400, response.getStatus());
         Object entity = response.getEntity();
         Assert.assertTrue(entity instanceof ResteasyViolationException);
         ResteasyViolationException e = ResteasyViolationException.class
               .cast(entity);
         countViolations(e, 3, 0, 2, 0, 1, 0);
      }

      after();
   }

   @Test
   public void testValidateOnExecutionInvalidOneLevel_Class() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorOneLevel_Class.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }

   @Test
   public void testValidateOnExecutionInvalidTwoLevels_Class() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorTwoLevels_Class.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }

   @Test
   public void testValidateOnExecutionInvalidOneLevel_Interface() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorOneLevel_Interface.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }

   @Test
   public void testValidateOnExecutionInvalidTwoLevels_Interface() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorTwoLevels_Interface.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }
   
   @Test
   public void testValidateOnExecutionInvalidGenericOverride_Class() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorWithGenericMethodSubClass.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }
   
   @Test
   public void testValidateOnExecutionInvalidGenericOverride_Interface() throws Exception
   {
      try
      {
         before(TestValidateOnExecutionErrorWithGenericSupermethod.class);
      }
      catch (ValidationException ve)
      {
         // OK
         return;
      }
      catch (Exception e)
      {
         fail("Unexpected excpetion: " + e);
      }
      fail("Expected ValidationException");
   }
   
   private void countViolations(ResteasyViolationException e,
         int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount, e.getViolations().size());
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
