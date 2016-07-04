package org.jboss.resteasy.test.nextgen.producers;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.ByteArrayOutputStream;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1282
 * 
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class ProducesMediaTest extends BaseResourceTest
{
   @Resource
   @Path("")
   public static class Hello
   {
      @Path("test")
      @Consumes(MediaType.APPLICATION_OCTET_STREAM)
      @POST
      public Response post() throws Exception {
         return Response.ok().entity(5).build();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Hello.class);
   }

   @Test
   public void test() throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
      for (int i = 0; i < 5000; i++) {
         baos.write(i);
      }
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/test")).request().post(Entity.entity(baos, MediaType.WILDCARD));
      String result = response.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("5", result);
   }
}
