package org.jboss.resteasy.test.resteasy1161;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.resteasy1161.StdQueryBeanParam;
import org.jboss.resteasy.resteasy1161.TestApplication;
import org.jboss.resteasy.resteasy1161.TestResource;
import org.jboss.resteasy.resteasy1161.TestSubResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for RESTEASY-1103.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date September 1, 2014
 */
@RunWith(Arquillian.class)
public class TestSubresourceValidation
{
//   private static final Logger log = LoggerFactory.getLogger(TestSubresourceValidation.class);
   
   @Deployment
   public static Archive<?> createTestArchive()
   {
      System.out.println("entering createTestArchive()");
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1161.war")
            .addClasses(TestApplication.class, TestResource.class, TestSubResource.class, StdQueryBeanParam.class)
            .addAsWebInfResource("1161/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testSubresource() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1161/sub/17?limit=abcdef").request();
      ClientResponse response = (ClientResponse) request.get();
      System.out.println("status: " + response.getStatus());
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      for(Iterator<ResteasyConstraintViolation> it = r.getParameterViolations().iterator(); it.hasNext(); )
      {
         System.out.println(it.next().toString());
      }
      countViolations(r, 0, 0, 0, 2, 0);
      assertEquals(400, response.getStatus());
   }
   
   @Test
   public void testReturnValue() throws Exception
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-1161/sub/return/abcd").request();
      ClientResponse response = (ClientResponse) request.get();
      System.out.println("status: " + response.getStatus());
      String answer = response.readEntity(String.class);
      ViolationReport r = new ViolationReport(answer);
      for(Iterator<ResteasyConstraintViolation> it = r.getParameterViolations().iterator(); it.hasNext(); )
      {
         System.out.println(it.next().toString());
      }
      countViolations(r, 0, 0, 0, 0, 1);
      assertEquals(500, response.getStatus());
   }
   
   protected void countViolations(ViolationReport e, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount)
   {
      Assert.assertEquals(fieldCount,       e.getFieldViolations().size());
      Assert.assertEquals(propertyCount,    e.getPropertyViolations().size());
      Assert.assertEquals(classCount,       e.getClassViolations().size());
      Assert.assertEquals(parameterCount,   e.getParameterViolations().size());
      Assert.assertEquals(returnValueCount, e.getReturnValueViolations().size());
   }
}
