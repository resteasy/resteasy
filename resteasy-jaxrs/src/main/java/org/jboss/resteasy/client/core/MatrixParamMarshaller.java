package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamMarshaller implements Marshaller
{
   private String paramName;
   private ResteasyProviderFactory factory;

   public MatrixParamMarshaller(String paramName, ResteasyProviderFactory factory)
   {
      this.paramName = paramName;
      this.factory = factory;
   }

   protected String toString(Object object)
   {
      StringConverter converter = factory.getStringConverter(object.getClass());
      if (converter != null) return converter.toString(object);
      else return object.toString();

   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      uri.matrixParam(paramName, toString(object));
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }

}