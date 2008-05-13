package org.jboss.resteasy.test.smoke;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Extension
{

   @Path("/extension")
   public static class ExtensionResource
   {
      @GET
      @ProduceMime("*/*")
      public String getDefault()
      {
         return "default";
      }

      @GET
      @ProduceMime("application/xml")
      public String getXml(@Context HttpHeaders headers)
      {
         List<String> languages = headers.getAcceptableLanguages();
         List<MediaType> mediaTypes = headers.getAcceptableMediaTypes();
         return "xml";
      }

      @GET
      @ProduceMime("text/html")
      public String getXmlTwo(@Context HttpHeaders headers)
      {
         List<String> languages = headers.getAcceptableLanguages();
         Assert.assertEquals(1, languages.size());
         Assert.assertEquals("en-US", languages.get(0));
         Assert.assertEquals(MediaType.valueOf("text/html"), headers.getAcceptableMediaTypes().get(0));
         return "html";
      }

      @GET
      @Path("/stuff.old")
      @ProduceMime("text/plain")
      public String getJson(@Context HttpHeaders headers)
      {
         List<String> languages = headers.getAcceptableLanguages();
         Assert.assertEquals(1, languages.size());
         Assert.assertEquals("en-US", languages.get(0));
         Assert.assertEquals(MediaType.valueOf("text/plain"), headers.getAcceptableMediaTypes().get(0));
         return "plain";
      }
   }

}