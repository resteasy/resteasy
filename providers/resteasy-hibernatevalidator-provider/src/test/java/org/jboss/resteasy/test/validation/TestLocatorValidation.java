package org.jboss.resteasy.test.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $Revision: 1.1 $
*
* Created August 14, 2013
*/
public class TestLocatorValidation
{
   private static final Logger log = LoggerFactory.getLogger(TestLocatorValidation.class);
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @Target(TYPE)
   @Retention(RUNTIME)
   @Constraint(validatedBy = SumValidator.class)
   public @interface SumConstraint
   {
      String message() default "{org.jboss.resteasy.ejb.validation.SumConstraint}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int min() default 0;
   }
   
   @ApplicationScoped
   public class SumValidator implements ConstraintValidator<SumConstraint, TestResource>
   {  
      private int min;
      
      public void initialize(SumConstraint constraint)
      {
         min = constraint.min();
      }

      @Override
      public boolean isValid(TestResource value, ConstraintValidatorContext context)
      {
         System.out.println("entering SumValidator.isValid(): min: " + min);
         System.out.println("field: " + value.field + ", property: " + value.getProperty());
         int sum = value.field + value.getProperty();
         return min <= sum;
      }
   }
   
   @Path("/")
   @SumConstraint(min = 9)
   @ValidateRequest
   public static class TestResource
   {
      @Min(3)
      @PathParam("field")
      protected int field;
      
      private int property;

      @Min(5)
      public int getProperty()
      {
         return property;
      }
      
      @PathParam("property") 
      public void setProperty(int property)
      {
         this.property = property;
         System.out.println("property: " + property);
      }
      
      @Path("locator/{field}/{property}/{param}")
      @Produces(MediaType.TEXT_PLAIN)
      public Object locator(@Min(11) @PathParam("param") int param)
      {
         System.out.println("TestResource.this: " + this);
         return new TestSubResource();
      }
   }
   
   @ValidateRequest
   public static class TestSubResource
   {
      @GET
      @Produces(MediaType.TEXT_PLAIN)
      @Path("{subparam}")
      @Min(17)
      public int submethod(@Min(13) @PathParam("subparam") int subparam)
      {
         System.out.println("Subresource.this: " + this);
         System.out.println("Subresource.submethod() returning " + subparam);
         return subparam;
      }
   }
   
   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   //////////////////////////////////////////////////////////////////////////////
//   @Test
//   public void testGetter() throws Exception
//   {
//      before(TestResource.class);
      ClientRequest request = new ClientRequest(generateURL("/resource/executable/getter"));
//      request.accept(MediaType.APPLICATION_XML);
//      ClientResponse<?> response = request.get(ViolationReport.class);
//      ViolationReport report = response.getEntity(ViolationReport.class);
//      System.out.println("report: " + report.toString());
//      countViolations(report, 1, 0, 1, 0, 0, 0);
//      after();
//   }
   
//   @Test
   public void testLocatorAllValid() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8081/RESTEASY-1008/locator/5/7/17/19").request();
      
      ClientRequest request = new ClientRequest(generateURL("/locator/5/7/17/19"));      
      ClientResponse<?> response = request.get();
      int result = response.getEntity(int.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + result);
      assertEquals(200, response.getStatus());
      assertEquals(19, result);
   }
   
   @Ignore
   @Test
   public void testLocatorInvalidParameter() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8081/RESTEASY-1008/locator/5/7/0/15").request();
      
      ClientRequest request = new ClientRequest(generateURL("/locator/5/7/0/15")); 
//      request.accept(MediaType.APPLICATION_XML);
//      ClientResponse<?> response = request.get(ViolationReport.class);
//      ViolationReport report = response.getEntity(ViolationReport.class);
      ClientResponse<?> response = request.get();
      String report = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(report));
      log.info("status: " + response.getStatus());
      log.info("entity: " + report);
      assertEquals(400, response.getStatus());
//      countViolations(report, 1, 0, 0, 0, 1, 0);
//      ResteasyConstraintViolation cv = report.getParameterViolations().iterator().next();
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 11"));
   }
   
   @Ignore
   @Test
   public void testLocatorInvalidSubparameter() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8081/RESTEASY-1008/locator/5/7/13/0").request();
      ClientRequest request = new ClientRequest(generateURL("/locator/5/7/13/0")); 
//      request.accept(MediaType.APPLICATION_XML);
//      ClientResponse<?> response = request.get(ViolationReport.class);
//      ViolationReport report = response.getEntity(ViolationReport.class);
      ClientResponse<?> response = request.get();
      String report = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(report));
      log.info("status: " + response.getStatus());
      log.info("entity: " + report);
      assertEquals(400, response.getStatus());
//      countViolations(report, 1, 0, 0, 0, 1, 0);
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 13"));
   }
   
   @Ignore
   @Test
   public void testLocatorInvalidReturnValue() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8081/RESTEASY-1008/locator/5/7/13/15").request();
      ClientRequest request = new ClientRequest(generateURL("/locator/5/7/13/15")); 
//      request.accept(MediaType.APPLICATION_XML);
//      ClientResponse<?> response = request.get(ViolationReport.class);
//      ViolationReport report = response.getEntity(ViolationReport.class);
      ClientResponse<?> response = request.get();
      String report = response.getEntity(String.class);
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(report));
      log.info("status: " + response.getStatus());
      log.info("entity: " + report);
      assertEquals(500, response.getStatus());
//      countViolations(report, 1, 0, 0, 0, 0, 1);
//      ResteasyConstraintViolation cv = report.getReturnValueViolations().iterator().next();
      countViolations(e, 1, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 17"));
   }
   
//   private void countViolations(ViolationReport e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
//   {
//      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
//      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
//      Assert.assertEquals(classCount, e.getClassViolations().size());
//      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
//      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
//   }
   protected void countViolations(ResteasyViolationException e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(totalCount,       e.getViolations().size());
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}