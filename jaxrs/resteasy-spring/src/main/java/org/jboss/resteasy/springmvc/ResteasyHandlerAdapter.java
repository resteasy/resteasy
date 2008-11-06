package org.jboss.resteasy.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.DispatcherUtilities;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocator;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/
// TODO: Right now there's a problematic relationship between Dispatcher and
// Registry. Ideally, the Registry shouldn't be owned by the Dispatcher, and the
// methods needed from SynchronousDispatcher should move into a shared class.
public class ResteasyHandlerAdapter implements HandlerAdapter
{

   private SynchronousDispatcher dispatcher;

   public ResteasyHandlerAdapter(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public long getLastModified(HttpServletRequest request, Object handler)
   {
      return 0;
   }

   public ModelAndView handle(HttpServletRequest servletRequest,
         HttpServletResponse servletResponse, Object handler) throws Exception
   {

      ResteasyRequestWrapper responseWrapper = (ResteasyRequestWrapper) handler;

      HttpResponse response = new HttpServletResponseWrapper(servletResponse,
            dispatcher.getProviderFactory());

      try
      {
         ResteasyProviderFactory.pushContext(HttpServletRequest.class,
               servletRequest);
         ResteasyProviderFactory.pushContext(HttpServletResponse.class,
               servletResponse);
         ResteasyProviderFactory.pushContext(SecurityContext.class,
               new ServletSecurityContext(servletRequest));

         // TODO: copied from SynchronousDispatcher!
         HttpRequest request = responseWrapper.getHttpRequest();
         dispatcher.getDispatcherUtilities().pushContextObjects(request,
               response);

         Response jaxrsResponse = null;
         try
         {
            ResourceInvoker invoker = responseWrapper.getInvoker();
            if (invoker instanceof ResourceMethod)
            {
               ReflectionUtils.makeAccessible(((ResourceMethod) invoker)
                     .getMethod());
            }
            else if (invoker instanceof ResourceLocator)
            {
               ReflectionUtils.makeAccessible(((ResourceLocator) invoker)
                     .getMethod());
            }
            jaxrsResponse = invoker.invoke(request, response);
         }
         catch (Exception e)
         {
            dispatcher.handleInvokerException(request, response, e);
         }

         try
         {
            if (jaxrsResponse != null)
               return createModelAndView(response, jaxrsResponse);
         }
         catch (Exception e)
         {
            dispatcher.handleWriteResponseException(request, response, e);
         }

      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
      return null;
   }

   public ModelAndView createModelAndView(HttpResponse response,
         Response jaxrsResponse) throws IOException, WebApplicationException
   {
      final DispatcherUtilities dispatcherUtilities = dispatcher
            .getDispatcherUtilities();
      dispatcherUtilities.writeCookies(response, jaxrsResponse);

      if (jaxrsResponse.getEntity() == null)
      {
         response.setStatus(jaxrsResponse.getStatus());
         dispatcherUtilities.outputHeaders(response, jaxrsResponse);
         return null;
      }

      dispatcherUtilities.writeCookies(response, jaxrsResponse);
      ResponseInvoker responseInvoker = new ResponseInvoker(
            dispatcherUtilities, jaxrsResponse);

      if (!(responseInvoker.getEntity() instanceof ModelAndView)
            && responseInvoker.getWriter() == null)
      {
         throw new LoggableFailure(
               String
                     .format(
                           "Could not find MessageBodyWriter for response object of type: %s of media type: %s",
                           responseInvoker.getType().getName(), responseInvoker
                                 .getContentType()),
               HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      }

      response.setStatus(jaxrsResponse.getStatus());
      dispatcherUtilities.outputHeaders(response, jaxrsResponse);

      String contentLength = String.valueOf(responseInvoker.getResponseSize());
      response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH,
            contentLength);

      if (responseInvoker.getEntity() instanceof ModelAndView)
      {
         return (ModelAndView) responseInvoker.getEntity();
      }
      else
      {
         return createModelAndView(responseInvoker);
      }
   }

   protected ModelAndView createModelAndView(ResponseInvoker responseInvoker)
   {
      return new ModelAndView(createView(responseInvoker))
            .addObject(responseInvoker);
   }

   protected ResteasyView createView(ResponseInvoker responseInvoker)
   {
      String contentType = responseInvoker.getContentType().toString();
      return new ResteasyView(contentType, dispatcher);
   }

   public boolean supports(Object handler)
   {
      return handler instanceof ResteasyRequestWrapper;
   }

}
