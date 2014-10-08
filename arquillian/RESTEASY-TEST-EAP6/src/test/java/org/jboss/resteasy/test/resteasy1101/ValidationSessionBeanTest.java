package org.jboss.resteasy.test.resteasy1101;

import javax.transaction.Status;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1101.SessionApplication;
import org.jboss.resteasy.resteasy1101.SessionResourceImpl;
import org.jboss.resteasy.resteasy1101.SessionResourceLocal;
import org.jboss.resteasy.resteasy1101.SessionResourceParent;
import org.jboss.resteasy.resteasy1101.SessionResourceRemote;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * RESTEASY-1101
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2014
 */
@RunWith(Arquillian.class)
public class ValidationSessionBeanTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1101.war")
            .addClasses(SessionApplication.class)
            .addClasses(SessionResourceParent.class)
            .addClasses(SessionResourceLocal.class, SessionResourceRemote.class, SessionResourceImpl.class)
            .addAsWebInfResource("1101/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testInvalidParam() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1101/test/resource");
      request.queryParameter("param", "abc");
      ClientResponse<?> response = request.get();
      String answer = response.getEntity(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("entity: " + answer);
      assertEquals(500, response.getStatus());
      assertTrue(answer.contains("size must be between 4 and "));
   }
}
