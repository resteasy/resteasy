package org.jboss.resteasy.test.validation;

import junit.framework.Assert;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
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
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created February 10, 2016
 */
@RunWith(Arquillian.class)
public class TestValidationJAXB
{  
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "Validation-test.war")
            .addClasses(JaxRsActivator.class)
            .addClasses(Foo.class, FooConstraint.class, FooReaderWriter.class, FooValidator.class)
            .addClasses(TestClassConstraint.class, TestClassValidator.class)
            .addClasses(TestResourceWithAllViolationTypes.class, TestResourceWithReturnValues.class)
            .addClass(TestValidationSuppressPathParent.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testRawXML() throws Exception
   {
      doRawTest(MediaType.APPLICATION_XML_TYPE, "<fieldViolations><constraintType>FIELD</constraintType><path>s</path>");
   }
   
   @Test
   public void testRawJSON() throws Exception
   {
      doRawTest(MediaType.APPLICATION_JSON_TYPE, "\"fieldViolations\":[{\"constraintType\":\"FIELD\",\"path\":\"s\"");
   }   
   
   @Test
   public void testXML() throws Exception
   {
      doTest(MediaType.APPLICATION_XML_TYPE);
   }
   
   @Test
   public void testJSON() throws Exception
   {
      doTest(MediaType.APPLICATION_JSON_TYPE);
   }
   
   public void doTest(MediaType mediaType) throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/all/a/z");
      Foo foo = new Foo("p");
      request.body("application/foo", foo);
      request.accept(mediaType);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      ViolationReport r = response.getEntity(ViolationReport.class);
      countViolations(r, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation violation = r.getFieldViolations().iterator().next();
      System.out.println("field path: " + violation.getPath());
      Assert.assertEquals("s", violation.getPath());
      violation = r.getPropertyViolations().iterator().next();
      System.out.println("property path: " + violation.getPath());
      Assert.assertEquals("t", violation.getPath());
      violation = r.getClassViolations().iterator().next();
      System.out.println("class path: " + violation.getPath());
      Assert.assertEquals("", violation.getPath());
      violation = r.getParameterViolations().iterator().next();
      System.out.println("parameter path: " + violation.getPath());
      Assert.assertEquals("post.arg0", violation.getPath());
   }

   public void doRawTest(MediaType mediaType, String expected) throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/Validation-test/rest/all/a/z");
      Foo foo = new Foo("p");
      request.body("application/foo", foo);
      request.accept(mediaType);
      ClientResponse<?> response = request.post(Foo.class);
      Assert.assertEquals(400, response.getStatus());
      String header = response.getResponseHeaders().getFirst(Validation.VALIDATION_HEADER);
      Assert.assertNotNull(header);
      Assert.assertTrue(Boolean.valueOf(header));
      String report = response.getEntity(String.class);
      System.out.println("raw report: " + report);
      Assert.assertTrue(report.contains(expected));
   }
   private void countViolations(ViolationReport r, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount,       r.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    r.getPropertyViolations().size());
      Assert.assertEquals(classCount,       r.getClassViolations().size());
      Assert.assertEquals(parameterCount,   r.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, r.getReturnValueViolations().size());
   }
}
