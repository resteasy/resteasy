package org.jboss.resteasy.test.nextgen.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Hashtable;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolationException;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-1137
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 11, 2015
 */
public class TestCustomExceptionMapper
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @Provider
   public static class CustomExceptionMapper implements ExceptionMapper<ConstraintViolationException>
   {
      @Override
      public Response toResponse(ConstraintViolationException exception)
      {
         System.out.println("CustomExceptionMapper.toResponse()");
         ResteasyViolationException rve = new ResteasyViolationException(exception.getConstraintViolations());
         TestReport report = new TestReport();
         report.setFieldViolations(rve.getFieldViolations().size());
         report.setPropertyViolations(rve.getPropertyViolations().size());
         report.setClassViolations(rve.getClassViolations().size());
         report.setParameterViolations(rve.getParameterViolations().size());
         report.setReturnValueViolations(rve.getReturnValueViolations().size());
         ResponseBuilder builder = Response.status(444);
         builder.type(MediaType.APPLICATION_XML_TYPE);
         builder.entity(report);
         return builder.build();
      }
   }
   
   @XmlRootElement(name="testReport")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class TestReport
   {
      private int fieldViolations;
      private int propertyViolations;
      private int classViolations;
      private int parameterViolations;
      private int returnValueViolations;
      public int getFieldViolations()
      {
         return fieldViolations;
      }
      public void setFieldViolations(int fieldViolations)
      {
         this.fieldViolations = fieldViolations;
      }
      public int getPropertyViolations()
      {
         return propertyViolations;
      }
      public void setPropertyViolations(int propertyViolations)
      {
         this.propertyViolations = propertyViolations;
      }
      public int getClassViolations()
      {
         return classViolations;
      }
      public void setClassViolations(int classViolations)
      {
         this.classViolations = classViolations;
      }
      public int getParameterViolations()
      {
         return parameterViolations;
      }
      public void setParameterViolations(int parameterViolations)
      {
         this.parameterViolations = parameterViolations;
      }
      public int getReturnValueViolations()
      {
         return returnValueViolations;
      }
      public void setReturnValueViolations(int returnValueViolations)
      {
         this.returnValueViolations = returnValueViolations;
      }
   }
   
   @Path("/")
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

   @Before
   public void before() throws Exception
   {
      Hashtable<String, String> initParams = new Hashtable<String, String>();
      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      deployment = EmbeddedContainer.start(initParams, contextParams);
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
      deployment.getProviderFactory().registerProvider(CustomExceptionMapper.class);
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Test
   public void testExceptionMapperInputViolations() throws Exception
   {  
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(TestPortProvider.generateURL("/all/a/b/c")).request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(444, response.getStatus());
      TestReport report = response.readEntity(TestReport.class);
      countViolations(report, 1, 1, 1, 1, 0);
   }
   
   @Test
   public void testExceptionMapperOutputViolations() throws Exception
   {
      Client client = ClientBuilder.newClient();
      Builder builder = client.target(TestPortProvider.generateURL("/all/abc/defg/hijkl")).request();
      builder.accept(MediaType.APPLICATION_XML);
      Response response = builder.get();
      System.out.println("status: " + response.getStatus());
      Assert.assertEquals(444, response.getStatus());
      TestReport report = response.readEntity(TestReport.class);
      countViolations(report, 0, 0, 0, 0, 1);
   }
   
   
   protected boolean countViolations(TestReport report, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      return report.fieldViolations == fieldCount
            && report.propertyViolations == propertyCount
            && report.classViolations == classCount
            && report.parameterViolations == parameterCount
            && report.returnValueViolations == returnValueCount;
   }
}