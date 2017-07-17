package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterProcessor implements InvocationProcessor
{
   private Class type;
   private MediaType mediaType;
   private Type genericType;
   private Annotation[] annotations;

   public MessageBodyParameterProcessor(MediaType mediaType, Class type, Type genericType, Annotation[] annotations)
   {
      this.type = type;
      this.mediaType = mediaType;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void process(ClientInvocationBuilder invocation, Object param)
   {
      invocation.getInvocation().setEntity(Entity.entity(param == null? null : new GenericEntity<Object>(param, genericType), mediaType, annotations));
   }

   public void build(ClientRequest request, Object object)
   {
      request.body(mediaType, object, type, genericType, annotations);
   }

   public Class getType()
   {
      return type;
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

}