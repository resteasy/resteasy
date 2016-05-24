package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

/**
 * This class represents the method level creation of a "rich response object"
 * that has the @ResponseObject annotation. These EntityExtractors will be used
 * to implment methods of ResponseObject via ResponseObjectEntityExtractor
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractor , ResponseObjectEntityExtractor
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
               public Integer extractEntity(ClientContext context, Object... args)
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
            public Object extractEntity(ClientContext context, Object... args)
            {
               return context.getClientResponse().getHeaderString(headerParam.value());
            }
         };
      }

      final LinkHeaderParam link = method.getAnnotation(LinkHeaderParam.class);
      if (link != null)
      {
         return processLinkHeader(method, returnType, link);
      }

      if (Response.class.isAssignableFrom(returnType))
      {
         return clientResponseExtractor;
      }

      return null;
   }

   private EntityExtractor processLinkHeader(final Method method, final Class<?> returnType,
                                             final LinkHeaderParam link)
   {
      if ("".equals(link.rel()) && "".equals(link.title()))
      {
         throw new RuntimeException(Messages.MESSAGES.mustSetLinkHeaderParam(method.getClass().getName(), method.getName()));
      }
      if (!"".equals(link.rel()) && !"".equals(link.title()))
      {
         throw new RuntimeException(Messages.MESSAGES.canOnlySetOneLinkHeaderParam(method.getClass().getName(), method.getName()));
      }

      if (returnType == Link.class)
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientContext context, Object... args)
            {
               return getLink(link, context);
            }
         };
      }

      if (isInvokerMethod(method))
      {
         return new EntityExtractor()
         {
            public Object extractEntity(ClientContext context, Object... args)
            {
               URI uri = getURI(method, link, context);
               if (uri == null)
                  return null;

               return createClientInvoker(context, uri, method).invoke(args);
            }
         };
      }

      if (returnType == String.class)
      {
         return new EntityExtractor<String>()
         {
            public String extractEntity(ClientContext context, Object... args)
            {
               Link link2 = getLink(link, context);
               return link2 == null ? null : link2.getUri().toString();
            }
         };
      }

      if (returnType == URL.class)
      {
         return new EntityExtractor<URL>()
         {
            public URL extractEntity(ClientContext context, Object... args)
            {
               return getURL(method, link, context);
            }
         };
      }
      if (returnType == URI.class)
      {
         return new EntityExtractor<URI>()
         {
            public URI extractEntity(ClientContext context, Object... args)
            {
               return getURI(method, link, context);
            }
         };
      }

      if (returnType.equals(Invocation.Builder.class))
      {
         return new EntityExtractor<Invocation.Builder>()
         {
            public Invocation.Builder extractEntity(ClientContext context, Object... args)
            {
               return context.getInvocation().getClient().target(getLink(link, context)).request();
            }
         };
      }

      if (returnType.equals(WebTarget.class))
      {
         return new EntityExtractor<WebTarget>()
         {
            public WebTarget extractEntity(ClientContext context, Object... args)
            {
               return context.getInvocation().getClient().target(getLink(link, context));
            }
         };
      }
      return null;
   }

   private ClientInvoker createClientInvoker(ClientContext context, URI uri, Method method) {
      ClientInvoker clientInvoker = new ClientInvoker((ResteasyWebTarget)(context.getInvocation().getClient().target(uri)),
              method.getDeclaringClass(),
              method,
              new ProxyConfig(Thread.currentThread().getContextClassLoader(), null, null));

      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      clientInvoker.setHttpMethod(httpMethods.iterator().next());
      return clientInvoker;
   }

   private static boolean isInvokerMethod(Method method)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      return httpMethods != null && httpMethods.size() == 1;
   }

   private Link getLink(final LinkHeaderParam link, ClientContext context)
   {
      return context.getClientResponse().getLink(link.rel());
   }

   private URI getURI(final Method method, Link link)
   {
      if (link == null)
      {
         return null;
      }
      return link.getUri();
   }

   private URI getURI(final Method method, final LinkHeaderParam link, ClientContext context)
   {
      return getURI(method, getLink(link, context));
   }

   private URL getURL(final Method method, final LinkHeaderParam link, ClientContext context)
   {
      URI uri = getURI(method, link, context);
      try
      {
         return uri == null ? null : uri.toURL();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotCreateURL(uri.toASCIIString(), method.getClass().getName(), method.getName()), e);
      }
   }
}
