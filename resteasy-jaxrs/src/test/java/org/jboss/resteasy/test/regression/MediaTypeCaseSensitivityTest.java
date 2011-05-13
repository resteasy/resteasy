package org.jboss.resteasy.test.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * RESTEASY-207 MediaType case sensistivity when matching MessageBodyReader
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeCaseSensitivityTest extends BaseResourceTest
{
   public static class Stuff
   {
      private String name;

      public Stuff(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }

   @Provider
   @Consumes("appLication/stUff")
   public static class StuffProvider implements MessageBodyReader<Stuff>
   {
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return type.equals(Stuff.class);
      }

      public Stuff readFrom(Class<Stuff> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
      {
         return new Stuff(ProviderHelper.readString(entityStream, mediaType));
      }
   }


   @Path("/stuff")
   public static class StuffResource
   {
      @POST
      public void post(Stuff stuff)
      {
         Assert.assertEquals(stuff.getName(), "bill");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(StuffResource.class);
      getProviderFactory().addMessageBodyReader(new StuffProvider());
   }


   @Test
   public void testIt() throws Exception
   {
      MessageBodyReader<Stuff> messageBodyReader = getProviderFactory().getMessageBodyReader(Stuff.class, Stuff.class,
              null, new MediaType("ApplIcAtion", "STufF"));
      Assert.assertNotNull(messageBodyReader);
      Assert.assertNotNull(messageBodyReader.getClass());
      Assert.assertEquals(StuffProvider.class, messageBodyReader.getClass());
      HttpClient client = new HttpClient();
      PostMethod post = new PostMethod(generateURL("/stuff"));
      post.setRequestEntity(new StringRequestEntity("bill", "Application/Stuff", null));
      int status = client.executeMethod(post);
      Assert.assertEquals(204, status);


   }

}
