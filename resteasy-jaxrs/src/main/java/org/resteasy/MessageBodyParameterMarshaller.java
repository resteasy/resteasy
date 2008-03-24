package org.resteasy;

import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.spi.ClientHttpOutput;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterMarshaller implements ParameterMarshaller
{
   private Class type;
   private ResteasyProviderFactory factory;
   private MediaType mediaType;
   private Type genericType;
   private Annotation[] annotations;

   public MessageBodyParameterMarshaller(MediaType mediaType, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.factory = factory;
      this.mediaType = mediaType;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   public void marshall(Object obj, UriBuilderImpl uri, ClientHttpOutput output)
   {
      try
      {
         MessageBodyWriter writer = getMessageBodyWriter();
         writer.writeTo(obj, type, genericType, annotations, mediaType, output.getOutputHeaders(), output.getOutputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failure marshalling body", e);
      }
   }

   public MessageBodyWriter getMessageBodyWriter()
   {
      MessageBodyWriter writer = factory.createMessageBodyWriter(type, genericType, annotations, mediaType);
      return writer;
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