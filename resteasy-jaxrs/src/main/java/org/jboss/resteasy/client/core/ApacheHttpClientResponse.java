package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;

import java.io.IOException;
import java.io.InputStream;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClientResponse<T> extends BaseClientResponse<T>
{

   protected HttpMethodBase httpMethod;

   public void setHttpMethod(HttpMethodBase httpMethod)
   {
      this.httpMethod = httpMethod;
   }


   @Override
   protected void finalize() throws Throwable
   {
      releaseConnection();
   }

   public InputStream getInputStream() throws IOException
   {
      return httpMethod.getResponseBodyAsStream();
   }

   public void releaseConnection()
   {
      if (!wasReleased)
      {
         if (httpMethod != null) httpMethod.releaseConnection();
         wasReleased = true;
      }
   }

}
