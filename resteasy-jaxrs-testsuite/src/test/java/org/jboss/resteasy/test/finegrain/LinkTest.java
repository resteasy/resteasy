package org.jboss.resteasy.test.finegrain;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Link;
import java.lang.reflect.Method;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilderException;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkTest
{
   @Test
   public void testRelativized() throws Exception
   {
      URI uri = new URI("a").relativize(new URI("a/d/e"));
      System.out.println(uri);

      Link link = Link.fromUri("a/d/e")
                      .rel("update").type("text/plain")
                      .buildRelativized(new URI("a"));
      System.out.println(link.toString());

      link = Link.fromUri("a/d/e")
              .rel("update").type("text/plain")
              .baseUri("http://localhost/")
              .buildRelativized(new URI("http://localhost/a"));
      System.out.println(link.toString());

   }


   @Path("resource")
   public static class Resource {

      @GET
      @Path("get")
      public String get() {
         return "GET";
      }

      @DELETE
      @Path("delete")
      public String delete() {
         return "DELETE";
      }

      @GET
      @Produces(MediaType.APPLICATION_SVG_XML)
      @Path("producessvgxml")
      public String producesSvgXml() {
         return MediaType.APPLICATION_SVG_XML;
      }

      @POST
      @Consumes(MediaType.APPLICATION_JSON)
      @Path("consumesappjson")
      public String consumesAppJson() {
         return MediaType.APPLICATION_JSON;
      }

      @POST
      @Produces({ MediaType.APPLICATION_XHTML_XML,
              MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_SVG_XML })
      @Path("producesxml")
      public String producesXml() {
         return MediaType.APPLICATION_XHTML_XML;
      }

      @POST
      @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
      @Path("consumesform")
      public String consumesForm() {
         return MediaType.APPLICATION_FORM_URLENCODED;
      }

   }


   @Test
   public void testFromMethod()
   {
      for (Method m : Resource.class.getDeclaredMethods())
      {
         Link link = Link.fromMethod(Resource.class, m.getName()).build();
         String string = link.toString();
         System.out.println("initial: " + string);
         Link fromValueOf = Link.valueOf(string);
         Assert.assertEquals(string, fromValueOf.toString());
      }
   }

   @Test
   public void testIllegalUri()
   {
      Link.Builder builder = Link.fromUri("http://:@");
      try {
         Link link = builder.build();
         Assert.fail();
      } catch (UriBuilderException e) {
      }

   }
}
