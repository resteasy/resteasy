package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamMarshaller implements Marshaller
{
   private String paramName;
   private boolean encoded;

   public PathParamMarshaller(String paramName, boolean encoded)
   {
      this.paramName = paramName;
      this.encoded = encoded;
   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      uri.substitutePathParam(paramName, object, encoded);
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }

}
