package org.jboss.resteasy.test.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.GroupDefinitionException;
import javax.validation.GroupSequence;
import javax.validation.Payload;
import javax.validation.ValidationException;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.SerializableProvider;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.validation.Validation;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;

public class TestValidationExceptions
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   ///////////////////////////////////////////////////////////////////////////////////////
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

   
   ///////////////////////////////////////////////////////////////////////////////////////
   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface IncorrectConstraint { }
   
   public static class TestClassValidator implements ConstraintValidator<IncorrectConstraint, TestResourceWithIncorrectConstraint>
   {
      int length;

      public void initialize(IncorrectConstraint constraintAnnotation)
      {
      }

      public boolean isValid(TestResourceWithIncorrectConstraint value, ConstraintValidatorContext context)
      {
         return true;
      }
   }
   
   @Path("/")
   @IncorrectConstraint
   public static class TestResourceWithIncorrectConstraint
   {
      @POST
      public void test()
      {
      }
   }
   
   
   ///////////////////////////////////////////////////////////////////////////////////////
   @Path("/")
   public static class TestSuperResource
   {
      @POST
      public void test(String s)
      {
      }
   }

   @Path("/")
   public static class TestSubResourceWithInvalidOverride extends TestSuperResource
   {
      @POST
      public void test(@Size(max=3) String s)
      {
      }
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////
   public static class OtherValidationException extends ValidationException
   {
      private static final long serialVersionUID = 1L;
   }
   
   @Documented
   @Constraint(validatedBy = OtherValidationExceptionValidator.class)
   @Target({FIELD, METHOD, PARAMETER})
   @Retention(RUNTIME)
   public @interface OtherValidationExceptionConstraint
   {
      String message() default "Throws OtherValidationException";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      String value() default "";
   }
   
   public static class OtherValidationExceptionValidator implements ConstraintValidator<OtherValidationExceptionConstraint, String>
   {
      @Override
      public void initialize(OtherValidationExceptionConstraint constraintAnnotation)
      {
      }
      @Override
      public boolean isValid(String s, ConstraintValidatorContext context)
      {
         if ("ok".equals(s))
         {
            return true;
         }
         if ("fail".equals(s))
         {
            throw new OtherValidationException();
         }
         throw new OtherValidationException();
      }
   }
   
   @Path("/")
   public static class TestResourceWithOtherValidationException
   {
      @PathParam("s")
      @OtherValidationExceptionConstraint
      String s;
      
      @POST
      @Path("parameter/{s}")
      public Response testParameter(@OtherValidationExceptionConstraint String s)
      {
         return Response.ok().build();
      }
      
      @POST
      @Path("return/{s}")
      @OtherValidationExceptionConstraint
      public String testReturnValue()
      {
         return "abc";
      }
      
      @GET
      @Path("execution/{s}")
      public void testExecution()
      {
         throw new OtherValidationException();
      }
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////
   interface TestGroup1 {}
   interface TestGroup2 {}
   
   @Path("/")
   @GroupSequence({ TestGroup1.class, TestGroup2.class })
   public static class TestResourceWithInvalidConstraintGroup
   {
      private String s;
      
      @GET
      public String test()
      {
         return s;
      }
   }
   
   ///////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testConstraintDefinitionException() throws Exception
   {
      before(TestResourceWithIncorrectConstraint.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(500, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      MediaType mediaType = response.getMediaType();
      Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
      Object entity = response.getEntity(Serializable.class);
      System.out.println("entity: " + entity);
      Assert.assertTrue(entity instanceof ConstraintDefinitionException);
      after();
   }
   
   @Test
   public void testConstraintDeclarationException() throws Exception
   {
      before(TestSubResourceWithInvalidOverride.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.post();
      Assert.assertEquals(500, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      MediaType mediaType = response.getMediaType();
      Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
      Object entity = response.getEntity(Serializable.class);
      System.out.println("entity: " + entity);
      Assert.assertTrue(entity instanceof ConstraintDeclarationException);
      after();
   }
   
   @Test
   public void TestGroupDefinitionException() throws Exception
   {
      before(TestResourceWithInvalidConstraintGroup.class);

      // Valid
      ClientRequest request = new ClientRequest(generateURL("/"));
      ClientResponse<?> response = request.get(String.class);
      Assert.assertEquals(500, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      MediaType mediaType = response.getMediaType();
      Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
      Object entity = response.getEntity(Serializable.class);
      System.out.println("entity: " + entity);
      Assert.assertTrue(entity instanceof GroupDefinitionException);
      after();
   }
   
   @Test
   public void testOtherValidationException() throws Exception
   {
      before(TestResourceWithOtherValidationException.class);

      {
         // Exception thrown during validation of field.
         ClientRequest request = new ClientRequest(generateURL("/parameter/fail"));
         request.body(MediaType.TEXT_PLAIN, "abc");
         ClientResponse<?> response = request.post(String.class);
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         MediaType mediaType = response.getMediaType();
         Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
         Object entity = response.getEntity(Serializable.class);
         System.out.println("entity: " + entity);
         Assert.assertTrue(entity instanceof ValidationException);
         ValidationException ve = ValidationException.class.cast(entity);
         Assert.assertTrue(ve.getCause() instanceof OtherValidationException);
      }
      
      {
         // Exception thrown during validation of parameter.
         ClientRequest request = new ClientRequest(generateURL("/parameter/ok"));
         request.body(MediaType.TEXT_PLAIN, "abc");
         ClientResponse<?> response = request.post(String.class);
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         MediaType mediaType = response.getMediaType();
         Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
         Object entity = response.getEntity(Serializable.class);
         System.out.println("entity: " + entity);
         Assert.assertTrue(entity instanceof ValidationException);
         ValidationException ve = ValidationException.class.cast(entity);
         Assert.assertTrue(ve.getCause() instanceof OtherValidationException);
      }
      
      {
         // Exception thrown during validation of return value.
         ClientRequest request = new ClientRequest(generateURL("/return/ok"));
         request.body(MediaType.TEXT_PLAIN, "abc");
         ClientResponse<?> response = request.post(String.class);
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         MediaType mediaType = response.getMediaType();
         Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
         Object entity = response.getEntity(Serializable.class);
         System.out.println("entity: " + entity);
         Assert.assertTrue(entity instanceof ValidationException);
         ValidationException ve = ValidationException.class.cast(entity);
         Assert.assertTrue(ve.getCause() instanceof OtherValidationException);
      }
      
      {
         // Exception thrown by resource method.
         ClientRequest request = new ClientRequest(generateURL("/execution/ok"));
         ClientResponse<?> response = request.get(String.class);
         Assert.assertEquals(500, response.getStatus());
         String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
         Assert.assertNotNull(header);
         Assert.assertTrue(Boolean.valueOf(header));
         MediaType mediaType = response.getMediaType();
         Assert.assertEquals(SerializableProvider.APPLICATION_SERIALIZABLE_TYPE, mediaType);
         Object entity = response.getEntity(Serializable.class);
         System.out.println("entity: " + entity);
         Assert.assertTrue(entity instanceof OtherValidationException);
      }
      
      after();
   }
}
