package org.jboss.resteasy.test.resteasy1008;

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
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
import org.jboss.resteasy.plugins.validation.cdi.ResteasyValidationCdiInterceptor;
import org.jboss.resteasy.resteasy1008.SumConstraint;
import org.jboss.resteasy.resteasy1008.SumValidator;
import org.jboss.resteasy.resteasy1008.TestApplication;
import org.jboss.resteasy.resteasy1008.TestResource;
import org.jboss.resteasy.resteasy1008.TestSubResource;
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
public class CDIValidationSettersTrueTest extends CDIValidationTestParent
{
   private static final Logger log = LoggerFactory.getLogger(CDIValidationSettersTrueTest.class);
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1008.war")
            .addClasses(CDIValidationTestParent.class)
            .addClasses(TestApplication.class, TestResource.class, TestSubResource.class)
            .addClasses(SumConstraint.class, SumValidator.class)
            .addClass(ResteasyValidationCdiInterceptor.class)
            .addAsWebInfResource("context/true/web.xml")
            .addAsWebInfResource("beans.xml", "beans.xml")
            .addAsLibrary(new File("target/resteasy-validation-cdi-as7.jar")) // Search
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testResourceMethodSetter() throws Exception
   {
//      ResteasyClient client = new ResteasyClientBuilder().build();
//      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1008/setter/11/13/0").request();
//      Response response = request.post(Entity.entity(1, MediaType.TEXT_PLAIN));
//      String answer = response.readEntity(String.class);
//      
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1008/setter/11/13/0");
      request.body(MediaType.TEXT_PLAIN, 1);
      ClientResponse<?> response = request.post();
      String answer = response.getEntity(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + answer);
      assertEquals(400, response.getStatus());
      ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
      countViolations(e, 1, 0, 0, 0, 1, 0);
      ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
      Assert.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
   }
}
