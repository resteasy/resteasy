package org.jboss.resteasy.test.resteasy1008;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy1008.JaxRsActivator;
import org.jboss.resteasy.resteasy1008.OutOfBandResource;
import org.jboss.resteasy.resteasy1008.OutOfBandResourceIntf;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTEASY-1049
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 5, 2014
 */
@RunWith(Arquillian.class)
public class OutOfBandTest
{
   private static final Logger log = LoggerFactory.getLogger(OutOfBandTest.class);

	@Deployment
	public static Archive<?> createTestArchive()
	{
	   WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
	         .addClasses(JaxRsActivator.class)
	         .addClasses(OutOfBandResourceIntf.class, OutOfBandResource.class)
	         .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	   System.out.println(war.toString(true));
	   return war;
	}
	
	@Test
	public void testTimerInterceptor() throws Exception
	{
       log.info("starting testTimerInterceptor()");
       
      // Schedule timer.
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/rest/timer/schedule").request();
      Response response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      
      // Verify timer expired and timer interceptor was executed.
      client = new ResteasyClientBuilder().build();
      request = client.target("http://localhost:8080/RESTEASY-1008/rest/timer/test").request();
      response = request.get();
      log.info("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
	}
}
