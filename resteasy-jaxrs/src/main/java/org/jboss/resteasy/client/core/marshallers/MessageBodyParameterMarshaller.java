package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.client.ClientRequest;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterMarshaller implements Marshaller
{
   private Class type;
   private MediaType mediaType;
   private Type genericType;
   private Annotation[] annotations;

   public MessageBodyParameterMarshaller(MediaType mediaType, Class type, Type genericType, Annotation[] annotations)
   {
      this.type = type;
      this.mediaType = mediaType;
      this.genericType = genericType;
      this.annotations = annotations;
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