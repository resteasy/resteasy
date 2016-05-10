package org.jboss.resteasy.plugins.server;

import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.Encode;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.net.URI;

/**
 * Helper for creating HttpRequest implementations.  The async code is a fake implementation to work with
 * http engines that don't support async HTTP.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class BaseHttpRequest implements HttpRequest
{
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected ResteasyUriInfo uri;

   protected BaseHttpRequest(ResteasyUriInfo uri)
   {
      this.uri = uri;
   }

   @Override
   public ResteasyUriInfo getUri()
   {
      return uri;
   }


   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      if (decodedFormParameters != null)
      {
         formParameters = Encode.encode(decodedFormParameters);
         return formParameters;
      }
      MediaType mt = getHttpHeaders().getMediaType();
      if (mt.isCompatible(MediaType.valueOf("application/x-www-form-urlencoded")))
      {
         try
         {
            formParameters = FormUrlEncodedProvider.parseForm(getInputStream(), mt.getParameters().get(MediaType.CHARSET_PARAMETER));
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         throw new IllegalArgumentException(Messages.MESSAGES.requestMediaTypeNotUrlencoded());
      }
      return formParameters;
   }

   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = Encode.decode(getFormParameters());
      return decodedFormParameters;
   }

   public boolean isInitial()
   {
      return true;
   }


   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      uri.setRequestUri(requestUri);
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      uri.setUri(baseUri, requestUri);
   }


}
