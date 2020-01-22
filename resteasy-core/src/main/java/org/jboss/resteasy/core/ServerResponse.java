package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerResponse extends BuiltResponse
{
   public ServerResponse()
   {
   }

   public ServerResponse(final Object entity, final int status, final Headers<Object> metadata)
   {
      this.setEntity(entity);
      this.status = status;
      this.metadata = metadata;
   }

   public ServerResponse(final BuiltResponse response)
   {
      this.setEntity(response.getEntity());
      this.setAnnotations(response.getAnnotations());
      this.setStatus(response.getStatus());
      this.setMetadata(response.getMetadata());
      this.setEntityClass(response.getEntityClass());
      this.setGenericType(response.getGenericType());
      this.setReasonPhrase(response.getReasonPhrase());
   }

   @Override
   public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns)
   {
      throw new IllegalStateException(Messages.MESSAGES.entityNotBackedByInputStream());
   }
}
