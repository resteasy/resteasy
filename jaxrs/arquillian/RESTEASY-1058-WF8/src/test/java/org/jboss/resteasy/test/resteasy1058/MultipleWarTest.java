package org.jboss.resteasy.test.resteasy1058;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy1058.SumConstraint;
import org.jboss.resteasy.resteasy1058.SumValidator;
import org.jboss.resteasy.resteasy1058.TestApplication;
import org.jboss.resteasy.resteasy1058.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RESTEASY-1058
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 13, 2014
 */
@RunWith(Arquillian.class)
public class MultipleWarTest
{
   private static final Logger log = LoggerFactory.getLogger(MultipleWarTest.class);

   @Deployment(name="war1", order=1)
   public static Archive<?> createTestArchive1()
   {
      WebArchive war1 = ShrinkWrap.create(WebArchive.class, "RESTEASY-1058-1.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addAsWebInfResource("web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war1.toString(true));
      return war1;
   }

   @Deployment(name="war2", order=2)
   public static Archive<?> createTestArchive2()
   {
      WebArchive war2 = ShrinkWrap.create(WebArchive.class, "RESTEASY-1058-2.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addAsWebInfResource("web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war2.toString(true));
      return war2;
   }

   @Test
   public void testInputsInvalid() throws Exception
   {
      //      ResteasyClient client = new ResteasyClientBuilder().build();
      //      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1058-1/wait/1/2/3").request();
      //      Response response = request.get();
      //      String answer = response.readEntity(String.class);

      ClientRequest request1 = new ClientRequest("http://localhost:8080/RESTEASY-1058-1/test/0/0/0");
      ClientRequest request2 = new ClientRequest("http://localhost:8080/RESTEASY-1058-2/test/0/0/0");
      ClientResponse<?> response = null;
      for (int i = 1; i < 2; i++)
      {
         response = request1.get(); 
         log.info("status: " + response.getStatus());
         String answer = response.getEntity(String.class);
         log.info("entity: " + answer);
         assertEquals(400, response.getStatus());
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
         System.out.println(e.toString());
         countViolations(e, 4, 1, 1, 1, 1, 0);
         ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 3"));
         cv = e.getPropertyViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 5"));
         cv = e.getClassViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().indexOf("org.jboss.resteasy.resteasy1058.SumConstraint") > 0);
         cv = e.getParameterViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
         response.close();
         
         response = request2.get(); 
         log.info("status: " + response.getStatus());
         answer = response.getEntity(String.class);
         log.info("entity: " + answer);
         assertEquals(400, response.getStatus());
         e = new ResteasyViolationException(String.class.cast(answer));
         System.out.println(e.toString());
         countViolations(e, 4, 1, 1, 1, 1, 0);
         cv = e.getFieldViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 3"));
         cv = e.getPropertyViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 5"));
         cv = e.getClassViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().indexOf("org.jboss.resteasy.resteasy1058.SumConstraint") > 0);
         cv = e.getParameterViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
         response.close();
      }
   }

   @Test
   public void testReturnValueInvalid() throws Exception
   {
      //      ResteasyClient client = new ResteasyClientBuilder().build();
      //      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1058-wait/wait/5/7/30").request();
      //      Response response = request.get();

      ClientRequest request1 = new ClientRequest("http://localhost:8080/RESTEASY-1058-1/test/5/7/9");
      ClientRequest request2 = new ClientRequest("http://localhost:8080/RESTEASY-1058-2/test/5/7/9");
      ClientResponse<?> response = null;
      for (int i = 1; i < 2; i++)
      {
         response = request1.get();   
         String answer = response.getEntity(String.class);
         log.info("status: " + response.getStatus());
         log.info("entity: " + answer);
         assertEquals(500, response.getStatus());
         ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
         System.out.println(e.toString());
         countViolations(e, 1, 0, 0, 0, 0, 1);
         ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be less than or equal to 0"));
         response.close();
         
         response = request2.get();   
         answer = response.getEntity(String.class);
         log.info("status: " + response.getStatus());
         log.info("entity: " + answer);
         assertEquals(500, response.getStatus());
         e = new ResteasyViolationException(String.class.cast(answer));
         System.out.println(e.toString());
         countViolations(e, 1, 0, 0, 0, 0, 1);
         cv = e.getReturnValueViolations().iterator().next();
         Assert.assertTrue(cv.getMessage().equals("must be less than or equal to 0"));
         response.close();
      }
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
