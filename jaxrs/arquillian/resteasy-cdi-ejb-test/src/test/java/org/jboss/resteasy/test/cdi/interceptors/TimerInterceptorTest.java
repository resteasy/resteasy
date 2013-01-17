package org.jboss.resteasy.test.cdi.interceptors;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.cdi.interceptors.JaxRsActivator;
import org.jboss.resteasy.cdi.interceptors.TimerInterceptorResource;
import org.jboss.resteasy.cdi.interceptors.TimerInterceptorResourceIntf;
import org.jboss.resteasy.cdi.util.UtilityProducer;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This is a collection of tests addressed to the interactions of 
 * Resteasy, CDI, EJB, and so forth in the context of a JEE Application Server.
 * 
 * It tests the injection of a variety of beans into Resteasy objects.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@RunWith(Arquillian.class)
public class TimerInterceptorTest
{
   @Inject Logger log;

	@Deployment
	public static Archive<?> createTestArchive()
	{
	   WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-cdi-ejb-test.war")
	         .addClasses(JaxRsActivator.class, UtilityProducer.class)
	         .addClasses(TimerInterceptorResourceIntf.class, TimerInterceptorResource.class)
	         .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	   System.out.println(war.toString(true));
	   return war;
	}
	
	@Test
	public void testTimerInterceptor() throws Exception
	{
       log.info("starting testTimerInterceptor()");
       
      // Schedule timer.
      ClientRequest request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/timer/schedule");
      ClientResponse<?> response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      
      // Verify timer expired and timer interceptor was executed.
      request = new ClientRequest("http://localhost:8080/resteasy-cdi-ejb-test/rest/timer/test");
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      response.releaseConnection();
	}
}
