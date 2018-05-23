package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.Link;
import org.jboss.resteasy.client.LinkHeader;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

/**
 * This class represents the method level creation of a "rich response object"
 * that has the @ResponseObject annotation. These EntityExtractors will be used
 * to implment methods of ResponseObject via ResponseObjectEntityExtractor
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractor
 * @see ResponseObjectEntityExtractorFactory
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
               return context.getClientResponse().getResponseHeaders().getFirst(headerParam.value());
            }
         };
      }

      final LinkHeaderParam link = method.getAnnotation(LinkHeaderParam.class);
      if (link != null)
      {
         return processLinkHeader(method, returnType, link);
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

      return null;
   }

   private EntityExtractor processLinkHeader(final Method method, final Class<?> returnType,
                                             final LinkHeaderParam link)
   {
      if ("".equals(link.rel()) && "".equals(link.title()))
      {
         throw new RuntimeException(Messages.MESSAGES.mustSetLinkHeaderRelOrTitle(method.getClass().getName(), method.getName()));
      }
      if (!"".equals(link.rel()) && !"".equals(link.title()))
      {
         throw new RuntimeException(Messages.MESSAGES.canOnlySetLinkHeaderRelOrTitle(method.getClass().getName(), method.getName()));
      }

      if (returnType == Link.class)
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientRequestContext context, Object... args)
            {
               return getLink(link, context);
            }
         };
      }

      if (isInvokerMethod(method))
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientRequestContext context, Object... args)
            {
               URI uri = getURI(method, link, context);
               if (uri == null)
                  return null;

               ClientRequest request = context.getRequest();
               EntityExtractorFactory extractor = context.getExtractorFactory();
               ResteasyProviderFactory provider = request.getProviderFactory();
               ClientExecutor executor = request.getExecutor();
               return ProxyFactory.createClientInvoker(method.getDeclaringClass(), method, uri,
                       executor, provider, extractor).invoke(args);
            }
         };
      }

      if (returnType == String.class)
      {
         return new EntityExtractor<String>()
         {
            public String extractEntity(ClientRequestContext context, Object... args)
            {
               Link link2 = getLink(link, context);
               return link2 == null ? null : link2.getHref();
            }
         };
      }

      if (returnType == URL.class)
      {
         return new EntityExtractor<URL>()
         {
            public URL extractEntity(ClientRequestContext context, Object... args)
            {
               return getURL(method, link, context);
            }
         };
      }
      if (returnType == URI.class)
      {
         return new EntityExtractor<URI>()
         {
            public URI extractEntity(ClientRequestContext context, Object... args)
            {
               return getURI(method, link, context);
            }
         };
      }

      if (returnType.equals(ClientRequest.class))
      {
         return new EntityExtractor<ClientRequest>()
         {
            public ClientRequest extractEntity(ClientRequestContext context, Object... args)
            {
               URI uri = getURI(method, link, context);
               return uri == null ? null : context.getRequest().createSubsequentRequest(uri);
            }
         };
      }

      return null;
   }

   private static boolean isInvokerMethod(Method method)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      return httpMethods != null && httpMethods.size() == 1;
   }

   private Link getLink(final LinkHeaderParam link, ClientRequestContext context)
   {
      LinkHeader linkHeader = context.getClientResponse().getLinkHeader();
      if (!"".equals(link.rel()))
         return linkHeader.getLinkByRelationship(link.rel());
      else
         return linkHeader.getLinkByTitle(link.title());
   }

   private URI getURI(final Method method, Link link)
   {
      if (link == null)
      {
         return null;
      }
      try
      {
         return new URI(link.getHref());
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotCreateURI(link.getHref(), method.getClass().getName(), method.getName()), e);
      }
   }

   private URI getURI(final Method method, final LinkHeaderParam link, ClientRequestContext context)
   {
      return getURI(method, getLink(link, context));
   }

   private URL getURL(final Method method, final LinkHeaderParam link, ClientRequestContext context)
   {
      URI uri = getURI(method, link, context);
      try
      {
         return uri == null ? null : uri.toURL();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotCreateURI(uri.toASCIIString(), method.getClass().getName(), method.getName()), e);
      }
   }
}
