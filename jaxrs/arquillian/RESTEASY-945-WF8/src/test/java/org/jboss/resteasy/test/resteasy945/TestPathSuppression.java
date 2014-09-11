package org.jboss.resteasy.test.resteasy945;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy945.TestApplication;
import org.jboss.resteasy.resteasy945.TestClassConstraint;
import org.jboss.resteasy.resteasy945.TestClassValidator;
import org.jboss.resteasy.resteasy945.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-945
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 14, 2014
 */
@RunWith(Arquillian.class)
public class TestPathSuppression
{  
   @Deployment(name="default", order=1)
   public static Archive<?> createTestArchiveDefault()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-945-default.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addAsWebInfResource("web-default.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Deployment(name="false", order=2)
   public static Archive<?> createTestArchiveFalse()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-945-false.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addAsWebInfResource("web-false.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Deployment(name="true")
   public static Archive<?> createTestArchiveTrue()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-945-true.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addAsWebInfResource("web-true.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testInputSuppressPathDefault() throws Exception
   {
      doTestInputViolations("default", "s", "t", "", "test.arg0");
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
      doTestReturnValueViolations("default", "test.<return value>");
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
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-945-" + suppress + "/all/a/b/c");
      ClientResponse<?>  response = request.get();
      System.out.println("status: " + response.getStatus());
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      String answer = response.getEntity(String.class);
      System.out.println("entity: " + answer);
      assertEquals(400, response.getStatus());
      ViolationReport report = new ViolationReport(String.class.cast(answer));
      countViolations(report, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation violation = report.getFieldViolations().iterator().next();
      System.out.println("violation: " + violation);
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
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-945-" + suppress + "/all/aa/bbb/cccc");
      ClientResponse<?>  response = request.get();
      System.out.println("status: " + response.getStatus());
      Object header = response.getHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertTrue(header instanceof String);
      Assert.assertTrue(Boolean.valueOf(String.class.cast(header)));
      String answer = response.getEntity(String.class);
      System.out.println("entity: " + answer);
      assertEquals(500, response.getStatus());
      ViolationReport report = new ViolationReport(String.class.cast(answer));
      countViolations(report, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation violation = report.getReturnValueViolations().iterator().next();
      System.out.println("return value path: " + violation.getPath());
      Assert.assertEquals(returnValuePath, violation.getPath());
   }
   
   private void countViolations(ViolationReport report, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount, report.getFieldViolations().size());
      Assert.assertEquals(propertyCount, report.getPropertyViolations().size());
      Assert.assertEquals(classCount, report.getClassViolations().size());
      Assert.assertEquals(parameterCount, report.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, report.getReturnValueViolations().size());
   }
}