package org.jboss.resteasy.examples.springmvc;

import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ContactsTest
{

   @Path(ContactsResource.CONTACTS_URL)
   public interface ContactProxy
   {
      @Path("data")
      @POST
      @Consumes(MediaType.APPLICATION_XML)
      Response createContact(Contact contact);

      @GET
      @Produces(MediaType.APPLICATION_XML)
      Contact getContact(@ClientURI String uri);

      @GET
      String getString(@ClientURI String uri);
   }

   private static TJWSEmbeddedSpringMVCServer server;
   private static ContactProxy proxy;
   public static final String host = "http://localhost:8080/";
   private static ResteasyClient client;

   @BeforeClass
   public static void setup()
   {
      server = new TJWSEmbeddedSpringMVCServer(
              "classpath:springmvc-servlet.xml", 8080);
      server.start();
      client = new ResteasyClientBuilder().build();
      proxy = client.target(host).proxy(ContactProxy.class);
   }

   @AfterClass
   public static void end()
   {
      server.stop();
      client.close();
   }

   @Test
   public void testData()
   {
      Response response = proxy.createContact(new Contact("Solomon", "Duskis"));
      Assert.assertEquals(response.getStatus(), 201);
      String duskisUri = (String) response.getMetadata().getFirst(
              HttpHeaderNames.LOCATION);
      System.out.println(duskisUri);
      Assert.assertTrue(duskisUri.endsWith(ContactsResource.CONTACTS_URL
              + "/data/Duskis"));
      response.close();
      Assert
              .assertEquals("Solomon", proxy.getContact(duskisUri).getFirstName());
      response = proxy.createContact(new Contact("Bill", "Burkie"));
      response.close();
      System.out.println(proxy.getString(host + ContactsResource.CONTACTS_URL
              + "/data"));
   }

   @Ignore
   @Test
   public void readHTML()
   {
      System.out.println(proxy.getString(ContactsResource.CONTACTS_URL));
   }
}
