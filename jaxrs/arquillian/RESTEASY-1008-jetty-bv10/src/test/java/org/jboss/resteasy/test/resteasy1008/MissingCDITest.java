package org.jboss.resteasy.test.resteasy1008;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1008.TestApplication;
import org.jboss.resteasy.resteasy1008.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2013
 */
@RunWith(Arquillian.class)
public class MissingCDITest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testMissingCDIValid() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9090/RESTEASY-1008/test/17");
      ClientResponse<?> response = request.get();
      System.out.println("Status: " + response.getStatus());
      System.out.println("Result: " + response.getEntity(String.class));
      assertEquals(200, response.getStatus());
      Assert.assertEquals("17", response.getEntity(String.class));
   }
   
   @Test
   public void testMissingCDIInvalid() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9090/RESTEASY-1008/test/0");
      ClientResponse<?> response = request.get();
      System.out.println("Status: " + response.getStatus());
      System.out.println("Result: " + response.getEntity(String.class));
      String answer = response.getEntity(String.class);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(answer);
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
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
