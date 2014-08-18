package org.jboss.resteasy.test.resteasy923;

import javax.transaction.Status;
import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy923.SessionApplication;
import org.jboss.resteasy.resteasy923.SessionResourceLocal;
import org.jboss.resteasy.resteasy923.SessionResourceImpl;
import org.jboss.resteasy.resteasy923.SessionResourceParent;
import org.jboss.resteasy.resteasy923.SessionResourceRemote;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * RESTEASY-1008
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@RunWith(Arquillian.class)
public class ValidationSessionBeanTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-923.war")
            .addClasses(SessionApplication.class)
            .addClasses(SessionResourceParent.class)
            .addClasses(SessionResourceLocal.class, SessionResourceRemote.class, SessionResourceImpl.class)
            .addAsWebInfResource("sessionbean/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testInvalidParam() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-923/test/resource");
      request.queryParameter("param", "abc");
      ClientResponse<?> response = request.get();
      String answer = response.getEntity(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      System.out.println(cv.getMessage());
      Assert.assertTrue(cv.getMessage().startsWith("size must be between 4 and"));
      Assert.assertTrue(answer.contains("size must be between 4 and"));
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
