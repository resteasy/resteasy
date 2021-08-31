package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.util.FindAnnotation;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Produces("application/x-www-form-urlencoded")
@Consumes("application/x-www-form-urlencoded")
@ConstrainedTo(RuntimeType.SERVER)
public class ServerFormUrlEncodedProvider extends FormUrlEncodedProvider
{
   protected boolean useContainerParams;

   public ServerFormUrlEncodedProvider(final boolean useContainerParams)
   {
      this.useContainerParams = useContainerParams;
   }

   @Context
   HttpRequest request;


   @SuppressWarnings("rawtypes")
   @Override
   public MultivaluedMap readFrom(Class<MultivaluedMap> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      if (!useContainerParams) return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);

      boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
      if (encoded) return request.getFormParameters();
      else return request.getDecodedFormParameters();

   }
}
