package org.jboss.resteasy.test.nextgen.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import io.undertow.servlet.api.DeploymentInfo;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-945
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 23, 2014
 */
public class TestPathSuppression
{
   private static UndertowJaxrsServer server;
   
   @Documented
   @Constraint(validatedBy = TestClassValidator.class)
   @Target({TYPE})
   @Retention(RUNTIME)
   public @interface TestClassConstraint
   {
      String message() default "Concatenation of s and t must have length > {value}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
      int value();
   }
   
   public static class TestClassValidator implements ConstraintValidator<TestClassConstraint, TestResource>
   {
      int length;

      public void initialize(TestClassConstraint constraintAnnotation)
      {
         length = constraintAnnotation.value();
      }

      public boolean isValid(TestResource value, ConstraintValidatorContext context)
      {
         boolean b = value.retrieveS().length() + value.getT().length() >= length;
         return b;
      }
   }
   
   @Path("all")
   @TestClassConstraint(5)
   public static class TestResource
   {
      @Size(min=2, max=4)
      @PathParam("s")
      String s;

      private String t;

      @Size(min=3, max=5)  
      public String getT()
      {
         return t;
      }
      
      public String retrieveS()
      {
         return s;
      }

      @PathParam("t") 
      public void setT(String t)
      {
         this.t = t;
      }

      @GET
      @Path("{s}/{t}/{u}")
      @Size(max=3)
      public String test(@Size(min=4, max=6) @PathParam("u") String u)
      {
         return s + t + u;
      }
   }

   @ApplicationPath("")
   public static class TestApplication extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
   }

   @Test
   public void testInputSuppressPathDefault() throws Exception
   {
      doTestInputViolations(null, "s", "t", "", "test.arg0");
   }

   @Test
   public void testInputSuppressPathFalse() throws Exception
   {
      doTestInputViolations("false", "s", "t", "", "test.arg0");
   }
   
   @Test
   public void testInputSuppressPathTrue() throws Exception
   {
      doTestInputViolations("true", "*", "*", "*", "*");
   }
   
   @Test
   public void testReturnValueSuppressPathDefault() throws Exception
   {
      doTestReturnValueViolations(null, "test.<return value>");
   }

   @Test
   public void testReturnValueSuppressPathFalse() throws Exception
   {
      doTestReturnValueViolations("false", "test.<return value>");
   }
   
   @Test
   public void testReturnSuppressPathTrue() throws Exception
   {
      doTestReturnValueViolations("true", "*");
   }
   
   public void doTestInputViolations(String suppress, String fieldPath, String propertyPath, String classPath, String parameterPath) throws Exception
   {
      DeploymentInfo di = server.undertowDeployment(TestApplication.class);
      if (suppress != null)
      {
         di.addInitParameter("resteasy.validation.suppress.path", suppress);  
      }
      di.setDeploymentName("validate");
      di.setContextPath("/validate");
      server.deploy(di);
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(TestPortProvider.generateURL("/validate/all/a/b/c")).request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      ViolationReport report = response.readEntity(ViolationReport.class);
      countViolations(report, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation violation = report.getFieldViolations().iterator().next();
      System.out.println("field path: " + violation.getPath());
      Assert.assertEquals(fieldPath, violation.getPath());
      violation = report.getPropertyViolations().iterator().next();
      System.out.println("property path: " + violation.getPath());
      Assert.assertEquals(propertyPath, violation.getPath());
      violation = report.getClassViolations().iterator().next();
      System.out.println("class path: " + violation.getPath());
      Assert.assertEquals(classPath, violation.getPath());;
      violation = report.getParameterViolations().iterator().next();
      System.out.println("parameter path: " + violation.getPath());
      Assert.assertEquals(parameterPath, violation.getPath());
   }
   
   public void doTestReturnValueViolations(String suppress, String returnValuePath) throws Exception
   {
      DeploymentInfo di = server.undertowDeployment(TestApplication.class);
      if (suppress != null)
      {
         di.addInitParameter("resteasy.validation.suppress.path", suppress);  
      }
      di.setDeploymentName("validate");
      di.setContextPath("/validate");
      server.deploy(di);
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(TestPortProvider.generateURL("/validate/all/aa/bbb/cccc")).request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      ViolationReport report = response.readEntity(ViolationReport.class);
      countViolations(report, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation violation = report.getReturnValueViolations().iterator().next();
      System.out.println("return value path: " + violation.getPath());
      Assert.assertEquals(returnValuePath, violation.getPath());
   }
   
   private void countViolations(ViolationReport e, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}