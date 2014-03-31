package org.jboss.resteasy.test.resteasy1008;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy1008.SumConstraint;
import org.jboss.resteasy.resteasy1008.SumValidator;
import org.jboss.resteasy.resteasy1008.TestApplication;
import org.jboss.resteasy.resteasy1008.TestResource;
import org.jboss.resteasy.resteasy1008.TestSubResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RESTEASY-1008
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@RunWith(Arquillian.class)
public class CDIValidationTest
{
   private static final Logger log = LoggerFactory.getLogger(CDIValidationTest.class);
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClass(TestSubResource.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addAsWebInfResource("web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testAllValid() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/input/11/13/17").request();
      Response response = request.get();
      int answer = response.readEntity(int.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(200, response.getStatus());
      assertEquals(17, answer);
   }
   
   @Test
   public void testInputsInvalid() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/input/1/2/3").request();
      Response response = request.get();
      String answer = response.readEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 4, 1, 1, 1, 1, 0);
      ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 3"));
      cv = e.getPropertyViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 5"));
      cv = e.getClassViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().indexOf("org.jboss.resteasy.ejb.validation.SumConstraint") > 0);
      cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
   }

   @Test
   public void testReturnValueInvalid() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/input/5/7/9").request();
      Response response = request.get();
      String answer = response.readEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(500, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 11"));
   }
   
   @Test
   public void testLocatorAllValid() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/locator/5/7/17/19").request();
      Response response = request.get();
      int result = response.readEntity(int.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + result);
      assertEquals(200, response.getStatus());
      assertEquals(19, result);
   }

   @Ignore
   @Test
   public void testLocatorInvalidParameter() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/locator/5/7/0/15").request();
      Response response = request.get();
      String answer = response.readEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 11"));
   }
   
   @Ignore
   @Test
   public void testLocatorInvalidSubparameter() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/locator/5/7/13/0").request();
      Response response = request.get();
      String answer = response.readEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 13"));
   }
   
   @Test
   public void testLocatorInvalidReturnValue() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/locator/5/7/13/15").request();
      Response response = request.get();
      String answer = response.readEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(500, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 0, 1);
      ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 17"));
   }
   
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
