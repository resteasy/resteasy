package org.jboss.resteasy.test.resteasy1008;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1008.SessionApplication;
import org.jboss.resteasy.resteasy1008.SessionResource;
import org.jboss.resteasy.resteasy1008.SessionResourceImpl;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
public class CDIValidationSessionBeanTest
{
   private static final Logger log = LoggerFactory.getLogger(CDIValidationSessionBeanTest.class);
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
            .addClasses(SessionApplication.class, SessionResource.class, SessionResourceImpl.class)
            .addAsWebInfResource("sessionbean/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testInvalidParam() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/test/resource/0").request();
      
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1008/test/resource/0");
      ClientResponse<?> response = request.get();
      String answer = response.getEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
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
