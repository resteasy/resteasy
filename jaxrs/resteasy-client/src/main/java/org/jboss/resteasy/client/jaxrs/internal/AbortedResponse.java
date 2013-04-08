package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.specimpl.BuiltResponse;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbortedResponse extends ClientResponse
{
   protected InputStream is;

   public AbortedResponse(ClientConfiguration configuration, Response response)
   {
      super(configuration);
      setMetadata(response.getMetadata());
      setStatus(response.getStatus());
      setEntity(response.getEntity());
      if (response instanceof BuiltResponse)
      {
         BuiltResponse built = (BuiltResponse)response;
         setEntityClass(built.getEntityClass());
         setGenericType(built.getGenericType());
         setAnnotations(built.getAnnotations());
      }
   }

   @Override
   protected InputStream getInputStream()
   {
      if (is == null && entity != null && entity instanceof InputStream)
      {
         is = (InputStream)entity;
      }
      return is;
   }

   @Override
   protected void setInputStream(InputStream is)
   {
      this.is = is;
   }

   @Override
   protected void releaseConnection()
   {
      try
      {
         if (is != null) is.close();
      }
      catch (IOException e)
      {

      }
   }
}
