package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.QueryParam;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   private String encodedName;

   public QueryParamInjector(final Class<?> type, final Type genericType, final AccessibleObject target, final String paramName, final String defaultValue, final boolean encode, final Annotation[] annotations, final ResteasyProviderFactory factory)
   {
      super(type, genericType, paramName, QueryParam.class, defaultValue, target, annotations, factory);
      this.encode = encode;
      try
      {
         this.encodedName = URLDecoder.decode(paramName, StandardCharsets.UTF_8.name());
      }
      catch (UnsupportedEncodingException e)
      {
         throw new BadRequestException(Messages.MESSAGES.unableToDecodeQueryString());
      }
   }

   @Override
   protected void throwProcessingException(String message, Throwable cause)
   {
      throw new NotFoundException(message, cause);
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      if (encode)
      {
         List<String> list = request.getUri().getQueryParameters(false).get(encodedName);
         return extractValues(list);
      }
      else
      {
         List<String> list = request.getUri().getQueryParameters().get(paramName);
         return extractValues(list);

      }
   }

   @Override
   public Object inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectQueryParam());
   }


}
