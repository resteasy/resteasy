package org.jboss.resteasy.test.charset;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-1066.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Aug 13, 2014
 */
public abstract class TestCharsetParent
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static final MediaType APPLICATION_XML_UTF16_TYPE;
   protected static final MediaType TEXT_PLAIN_UTF16_TYPE;
   protected static final MediaType WILDCARD_UTF16_TYPE;
   protected static final String APPLICATION_XML_UTF16 = "application/xml;charset=UTF-16";
   protected static final String TEXT_PLAIN_UTF16 = "text/plain;charset=UTF-16";
   protected static final String WILDCARD_UTF16 = "*/*;charset=UTF-16";

   static
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("charset", "UTF-16");
      APPLICATION_XML_UTF16_TYPE = new MediaType("application", "xml", params);
      TEXT_PLAIN_UTF16_TYPE = new MediaType("text", "plain", params);
      WILDCARD_UTF16_TYPE = new MediaType("*", "*", params);
   }

   @Path("/")
   public static class MovieResource
   {
      @GET
      @Path("junk")
      public String junk()
      {
         return "junk";
      }

      @POST
      @Path("xml/produces")
      @Consumes("application/xml")
      @Produces(APPLICATION_XML_UTF16)
      public FavoriteMovieXmlRootElement xmlProduces(FavoriteMovieXmlRootElement movie)
      {
         System.out.println("server default charset: "
               + Charset.defaultCharset());
         System.out.println("title: " + movie.getTitle());
         return movie;
      }

      @POST
      @Path("xml/accepts")
      @Consumes("application/xml")
      public FavoriteMovieXmlRootElement xmlAccepts(FavoriteMovieXmlRootElement movie)
      {
         System.out.println("server default charset: "
               + Charset.defaultCharset());
         System.out.println("title: " + movie.getTitle());
         return movie;
      }

      @POST
      @Path("xml/default")
      @Consumes("application/xml")
      @Produces("application/xml")
      public FavoriteMovieXmlRootElement xmlDefault(FavoriteMovieXmlRootElement movie)
      {
         System.out.println("server default charset: "
               + Charset.defaultCharset());
         System.out.println("title: " + movie.getTitle());
         return movie;
      }
   }

   @XmlRootElement
   public static class FavoriteMovieXmlRootElement
   {
      private String _title;

      public String getTitle()
      {
         return _title;
      }

      public void setTitle(String title)
      {
         _title = title;
      }
   }

   @Test
   public void testXmlDefault() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xml/default"));
      String str = "<?xml version=\"1.0\"?>\r"
            + "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>";
      System.out.println(str);
      System.out.println("client default charset: " + Charset.defaultCharset());
      request.body(MediaType.APPLICATION_XML_TYPE, str);
      request.accept(MediaType.APPLICATION_XML_TYPE);
      System.out.println("Sending request");
      ClientResponse<?> response = request.post();
      System.out.println("Received response");
      Assert.assertEquals(200, response.getStatus());
      FavoriteMovieXmlRootElement entity = response.getEntity(FavoriteMovieXmlRootElement.class);
      System.out.println("Result: " + entity);
      System.out.println("title: " + entity.getTitle());
      Assert.assertEquals("La Règle du Jeu", entity.getTitle());
   }

   @Test
   public void testXmlProduces() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xml/produces"));
      String str = "<?xml version=\"1.0\"?>\r"
            + "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>";
      System.out.println(str);
      System.out.println("client default charset: " + Charset.defaultCharset());
      request.body(APPLICATION_XML_UTF16_TYPE, str);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      FavoriteMovieXmlRootElement entity = response.getEntity(FavoriteMovieXmlRootElement.class);
      System.out.println("Result: " + entity);
      System.out.println("title: " + entity.getTitle());
      Assert.assertEquals("La Règle du Jeu", entity.getTitle());
   }

   @Test
   public void testXmlAccepts() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xml/accepts"));
      String str = "<?xml version=\"1.0\"?>\r"
            + "<favoriteMovieXmlRootElement><title>La Règle du Jeu</title></favoriteMovieXmlRootElement>";
      System.out.println(str);
      System.out.println("client default charset: " + Charset.defaultCharset());
      request.body(APPLICATION_XML_UTF16_TYPE, str);
      request.accept(APPLICATION_XML_UTF16_TYPE);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(200, response.getStatus());
      FavoriteMovieXmlRootElement entity = response.getEntity(FavoriteMovieXmlRootElement.class);
      System.out.println("Result: " + entity);
      System.out.println("title: " + entity.getTitle());
      Assert.assertEquals("La Règle du Jeu", entity.getTitle());
   }
}