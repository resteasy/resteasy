package org.jboss.resteasy.client.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

public class WebRequestIntializer
{
   private Marshaller[] params;
   private Method method;

   public WebRequestIntializer(Method method, ResteasyProviderFactory providerFactory)
   {
      this.method = method;
      params = ClientMarshallerFactory.createMarshallers(method,
            providerFactory);
   }

   public WebRequestIntializer(Collection<Marshaller> marshallers)
   {
      this.params = marshallers.toArray(new Marshaller[0]);
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

   public String buildUrl(String uri, boolean allowRelative, Object... args) throws IllegalArgumentException, URISyntaxException
   {
      UriBuilderImpl builder = new UriBuilderImpl();
      int index = uri.indexOf("//");
      if( index != -1 )
      {
         int index2 = uri.indexOf("/", index + 2);
         builder.uri(new URI(uri.substring(0, index2)));
         index = index2;
      } 
      else
      {
         index = 0;
      }
      String[] segments = uri.substring(index).split("/");
      if(segments.length > 0 )
         builder.segment(segments);
      
      return buildUrl(builder, allowRelative, args);
   }
   
   public String buildUrl(URI uri, boolean allowRelative, Object... args)
   {
      UriBuilderImpl builder = new UriBuilderImpl();
      builder.uri(uri);
      if( method != null )
      {
         builder.path(method.getDeclaringClass());
         builder.path(method);
      }
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