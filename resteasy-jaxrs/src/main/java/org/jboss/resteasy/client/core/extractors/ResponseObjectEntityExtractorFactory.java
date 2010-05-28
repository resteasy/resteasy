package org.jboss.resteasy.client.core.extractors;

import java.lang.reflect.Method;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.LinkHeader;

/**
 * This class represents the method level creation of a "rich response object" that has the @ResponseObject annotation. 
 * These EntityExtractors will be used to implment methods of ResponseObject via ResponseObjectEntityExtractor
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @see EntityExtractor, ResponseObjectEntityExtractor
 */
public class ResponseObjectEntityExtractorFactory extends DefaultEntityExtractorFactory
{

   @SuppressWarnings("unchecked")
   public EntityExtractor createExtractor(final Method method)
   {
      final Class<?> returnType = method.getReturnType();
      if (method.isAnnotationPresent(Status.class))
      {
         if (returnType == Integer.class || returnType == int.class)
         {

            return new EntityExtractor<Integer>()
            {
               public Integer extractEntity(ClientRequestContext context, Object... args)
               {
                  return context.getClientResponse().getStatus();
               }
            };
         }
         else if (returnType == Response.Status.class)
         {
            return createStatusExtractor(false);
         }
      }

      if (method.isAnnotationPresent(Body.class))
      {
         return new BodyEntityExtractor(method);
      }

      final HeaderParam headerParam = method.getAnnotation(HeaderParam.class);
      if (headerParam != null)
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientRequestContext context, Object... args)
            {
               return context.getClientResponse().getHeaders().getFirst(headerParam.value());
            }
         };
      }

      if (returnType == ClientRequest.class)
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientRequestContext context, Object... args)
            {
               return context.getRequest();
            }
         };
      }

      if (Response.class.isAssignableFrom(returnType))
      {
         return createResponseTypeEntityExtractor(method);
      }

      if (returnType == LinkHeader.class)
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientRequestContext context, Object... args)
            {
               return context.getClientResponse().getLinkHeader();
            }
         };
      }
      
      // TODO: add processing for single @LinkHeader annotation to string together HTTP calls (HATEOAS client...) 
      
      return null;
   }

}
