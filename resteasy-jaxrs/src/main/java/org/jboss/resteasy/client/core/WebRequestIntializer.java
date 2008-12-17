package org.jboss.resteasy.client.core;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
/**
 * this class is used  
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */


public class WebRequestIntializer
{
   private Marshaller[] params;

   public WebRequestIntializer(Marshaller[] marshallers)
   {
      this.params = marshallers;
   }

   public Marshaller[] getParams()
   {
      return params;
   }

   public void setHeadersAndRequestBody(HttpMethodBase baseMethod, Object... args)
   {
      if (args != null)
      {
         for (int i = 0; i < args.length; i++)
         {
            params[i].setHeaders(args[i], baseMethod);
         }
         for (int i = 0; i < args.length; i++)
         {
            params[i].buildRequest(args[i], baseMethod);
         }
      }
   }

   public String buildUrl(String uriTemplate, boolean allowRelative, Object... args) throws IllegalArgumentException, URISyntaxException
   {
      UriBuilderImpl builder = new UriBuilderImpl();
      int index = uriTemplate.indexOf("//");
      if( index != -1 )
      {
         int index2 = uriTemplate.indexOf("/", index + 2);
         builder.uri(new URI(uriTemplate.substring(0, index2)));
         index = index2;
      } 
      else
      {
         index = 0;
      }
      String[] segments = uriTemplate.substring(index).split("/");
      if(segments.length > 0 )
         builder.segment(segments);
      
      return buildUrl(builder, allowRelative, args);
   }
   
   public String buildUrl(URI uri, boolean allowRelative, Method method, Object... args)
   {
      UriBuilderImpl builder = new UriBuilderImpl();
      builder.uri(uri)
         .path(method.getDeclaringClass())
         .path(method);
      return buildUrl(builder, allowRelative, args);
   }

   private String buildUrl(UriBuilderImpl builder, boolean allowRelative, Object... args)
   {
      if (args != null)
      {
         for (int i = 0; i < args.length; i++)
         {
            params[i].buildUri(args[i], builder);
         }
      }

      try
      {
         URI finalURI = builder.build();
         if (allowRelative)
            return finalURI.toString();
         else
            return finalURI.toURL().toString();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException("Unable to build URL from uri", e);
      }
   }
}