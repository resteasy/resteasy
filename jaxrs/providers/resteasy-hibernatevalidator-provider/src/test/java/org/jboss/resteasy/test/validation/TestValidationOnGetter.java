package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import junit.framework.Assert;

import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.Test;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $Revision: 1.1 $
*
* Created August 14, 2013
*/
public class TestValidationOnGetter
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.METHOD })
   @Retention(value = RetentionPolicy.RUNTIME)
   @Constraint(validatedBy = NotNullOrOneStringBeanValidator.class)
   public @interface NotNullOrOne
   {
      String message() default "{NotNullOrOne.message}";
      Class<?>[] groups() default {};
      Class<? extends Payload>[] payload() default {};
   }
   
   public static class NotNullOrOneStringBeanValidator implements ConstraintValidator<NotNullOrOne, StringBean>
   {
      @Override
      public void initialize(NotNullOrOne arg0)
      {
      }
      @Override
      public boolean isValid(StringBean bean, ConstraintValidatorContext context)
      {
         String value = bean.get();
         if (value == null || value.length() < 2)
            return false;
         return true;
      }
   }
   
   @NotNullOrOne
   public static class StringBean
   {
      private String header;

      public String get()
      {
         return header;
      }
      public void set(String header)
      {
         this.header = header;
      }
      public StringBean(String header)
      {
         this.header = header;
      }
   }
   
   @Path("resource/executable")
   @ValidateRequest
   public static class ValidateExecutableResource 
   {
      @Path("getter")
      @GET
      @Valid
      public StringBean getStringBean()
      {
         return new StringBean("1");
      }
   }
   
   //////////////////////////////////////////////////////////////////////////////
   public static void before(Class<?> resourceClass) throws Exception
   {
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

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGetter() throws Exception
   {
      before(ValidateExecutableResource.class);

      Client client = ClientBuilder.newClient();
      WebTarget target = client.target(generateURL("/resource/executable/getter"));
      Response response = target.request().get();
      ViolationReport report = response.readEntity(ViolationReport.class);
      response.close();

      countViolations(report, 1, 0, 1, 0, 0, 0);

      after();
   }
   
   private void countViolations(ViolationReport e, int totalCount, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount, e.getFieldViolations().size());
      Assert.assertEquals(propertyCount, e.getPropertyViolations().size());
      Assert.assertEquals(classCount, e.getClassViolations().size());
      Assert.assertEquals(parameterCount, e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}