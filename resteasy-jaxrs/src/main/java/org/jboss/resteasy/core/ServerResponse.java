package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.BuiltResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponse extends BuiltResponse
{
   public ServerResponse()
   {
   }

   public ServerResponse(Object entity, int status, Headers<Object> metadata)
   {
      this.setEntity(entity);
      this.status = status;
      this.metadata = metadata;
   }

   public ServerResponse(BuiltResponse response)
   {
      this.setEntity(response.getEntity());
      this.setAnnotations(response.getAnnotations());
      this.setStatus(response.getStatus());
      this.setMetadata(response.getMetadata());
      this.setEntityClass(response.getEntityClass());
      this.setGenericType(response.getGenericType());
      this.setReasonPhrase(response.getReasonPhrase());
   }
}
