package org.jboss.resteasy.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.DispatcherUtilities;
import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.UnhandledException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */
// TODO: Right now there's a problematic relationship between Dispatcher and
// Registry. Ideally, the Registry shouldn't be owned by the Dispatcher, and the
// methods needed from SynchronousDispatcher should move into a shared class.
public class ResteasyHandlerAdapter extends
      ResteasyWebHandlerTemplate<ModelAndView> implements HandlerAdapter
{

   public ResteasyHandlerAdapter(SynchronousDispatcher dispatcher)
   {
      super(dispatcher);
   }

   public long getLastModified(HttpServletRequest request, Object handler)
   {
      return 0;
   }

   public ModelAndView handle(HttpServletRequest servletRequest,
         HttpServletResponse servletResponse, Object handler) throws Exception
   {
      ResteasyRequestWrapper requestWrapper = (ResteasyRequestWrapper) handler;
      // super.handle wraps the handle call you see below
      return super.handle(requestWrapper, servletResponse);
   }

   protected ModelAndView handle(ResteasyRequestWrapper requestWrapper,
         HttpResponse response) throws IOException
   {
      if (requestWrapper.getErrorCode() != null)
      {
         try
         {
            response.sendError(requestWrapper.getErrorCode(), requestWrapper
                  .getErrorMessage());
         }
         catch (Exception e)
         {
            throw new UnhandledException(e);
         }
         return null;
      }
      HttpRequest request = requestWrapper.getHttpRequest();
      if (dispatcher instanceof AsynchronousDispatcher)
      {
         AsynchronousDispatcher asyncDispatcher = (AsynchronousDispatcher) dispatcher;
         if (asyncDispatcher.isAsynchrnousRequest(request))
         {
            asyncDispatcher.invoke(request, response);
            return null;
         }
      }
      return createModelAndView(requestWrapper, response);
   }

   protected ModelAndView createModelAndView(
         ResteasyRequestWrapper requestWrapper, HttpResponse response)
   {
      HttpRequest request = requestWrapper.getHttpRequest();
      DispatcherUtilities utils = dispatcher.getDispatcherUtilities();
      utils.pushContextObjects(request, response);

      Response jaxrsResponse = null;
      try
      {
         jaxrsResponse = requestWrapper.getInvoker().invoke(request, response);
      }
      catch (Exception e)
      {
         dispatcher.handleInvokerException(request, response, e);
      }

      if (jaxrsResponse == null)
         return null;

      try
      {
         ResponseInvoker responseInvoker = null;
         Object entity = jaxrsResponse.getEntity();
         if (entity instanceof ModelAndView)
         {
            utils.outputCookies(response, jaxrsResponse);
            utils.outputHeaders(response, jaxrsResponse);
            return (ModelAndView) entity;
         }
         responseInvoker = utils.resolveResponseInvoker(response, jaxrsResponse);
         return (responseInvoker == null) ? null : createModelAndView(responseInvoker);
      }
      catch (Exception e)
      {
         dispatcher.handleWriteResponseException(request, response, e);
         return null;
      }
   }

   protected ModelAndView createModelAndView(ResponseInvoker responseInvoker)
   {
      View view = createView(responseInvoker);
      return new ModelAndView(view, "responseInvoker", responseInvoker);
   }

   protected View createView(ResponseInvoker responseInvoker)
   {
      String contentType = responseInvoker.getContentType().toString();
      return new ResteasyView(contentType, dispatcher);
   }

   public boolean supports(Object handler)
   {
      return handler instanceof ResteasyRequestWrapper;
   }

}
