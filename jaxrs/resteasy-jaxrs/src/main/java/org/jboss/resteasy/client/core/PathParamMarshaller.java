package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamMarshaller implements Marshaller
{
   private String paramName;
   private boolean encoded;
   private ResteasyProviderFactory factory;

   public PathParamMarshaller(String paramName, boolean encoded, ResteasyProviderFactory factory)
   {
      this.paramName = paramName;
      this.encoded = encoded;
      this.factory = factory;
   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      StringConverter converter = factory.getStringConverter(object.getClass());
      if (converter != null)
      {
         uri.substitutePathParam(paramName, converter.toString(object), encoded);
      }
      else uri.substitutePathParam(paramName, object, encoded);
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }

}
