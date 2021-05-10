package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.CookieParamInjector;
import org.jboss.resteasy.plugins.spring.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.annotation.RestfulData;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.BadRequestException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This class can be used as part of Spring's AnnotationMethodHandlerAdapter.
 * Take a look at AnnotationMethodHandlerAdapter.setCustomArgumentResolver()
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public class ResteasyWebArgumentResolver implements WebArgumentResolver
{

   ResteasyProviderFactory factory;

   public ResteasyProviderFactory getFactory()
   {
      return factory;
   }

   public void setFactory(ResteasyProviderFactory factory)
   {
      this.factory = factory;
   }

   @SuppressWarnings("unchecked")
   public Object resolveArgument(MethodParameter methodParameter,
                                 NativeWebRequest webRequest) throws Exception
   {
      HttpServletRequest servletRequest = (HttpServletRequest) webRequest
              .getNativeRequest();
      Object[] parameterAnnotations = methodParameter.getParameterAnnotations();
      for (int i = 0; i < parameterAnnotations.length; i++)
      {
         Object annotation = parameterAnnotations[i];
         boolean isRestfulData = RestfulData.class.isInstance(annotation);
         boolean isCookie = CookieParam.class.isInstance(annotation);
         if (!isRestfulData && !isCookie)
            continue;

         HttpRequest request = RequestUtil.getHttpRequest(servletRequest);
         Class type = methodParameter.getParameterType();
         Method method = methodParameter.getMethod();
         Type genericType = method.getGenericParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];

         if (isRestfulData)
         {
            method.getTypeParameters();
            String contentType = servletRequest.getContentType();
            MediaType mediaType = MediaType.valueOf(contentType);
            MessageBodyReader reader = factory.getMessageBodyReader(type,
               genericType, annotations, mediaType);
            if (reader == null)
               throw new BadRequestException(Messages.MESSAGES.couldNotFindMessageBodyReader(genericType, mediaType));
            return reader.readFrom(type, genericType, annotations, mediaType,
               request.getHttpHeaders().getRequestHeaders(), request
                     .getInputStream());
         }
         else if (isCookie)
         {
            CookieParam cookieParam = (CookieParam) annotation;
            DefaultValue defaultValue = FindAnnotation.findAnnotation(
               annotations, DefaultValue.class);
            String defaultVal = null;
            if (defaultValue != null)
               defaultVal = defaultValue.value();

            return new CookieParamInjector(type, genericType, method,
               cookieParam.value(), defaultVal, annotations, factory).inject(request,
               null, false);
         }
      }
      return null;
   }
}
