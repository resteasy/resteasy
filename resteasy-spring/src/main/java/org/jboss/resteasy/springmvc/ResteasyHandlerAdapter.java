package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.UnhandledException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */
// TODO: Right now there's a problematic relationship between Dispatcher and
// Registry. Ideally, the Registry shouldn't be owned by the Dispatcher, and the
// methods needed from SynchronousDispatcher should move into a shared class.
public class ResteasyHandlerAdapter extends
        ResteasyWebHandlerTemplate<ModelAndView> implements HandlerAdapter
{
   protected SynchronousDispatcher dispatcher;

   public ResteasyHandlerAdapter(SynchronousDispatcher dispatcher)
   {
      super(dispatcher.getProviderFactory());
      this.dispatcher = dispatcher;
   }

   public long getLastModified(HttpServletRequest request, Object handler)
   {
      return -1;
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
      dispatcher.pushContextObjects(request, response);
      try
      {
         ServerResponse jaxrsResponse = null;
         try
         {
            jaxrsResponse = (ServerResponse) requestWrapper.getInvoker().invoke(request, response);
         }
         catch (Exception e)
         {
            dispatcher.handleInvokerException(request, response, e);
         }

         if (jaxrsResponse == null)
            return null;

         try
         {
            Object entity = jaxrsResponse.getEntity();
            if (entity instanceof ModelAndView)
            {
               jaxrsResponse.outputHeaders(response);
               return (ModelAndView) entity;
            }
            return (jaxrsResponse == null) ? null : createModelAndView(jaxrsResponse);
         }
         catch (Exception e)
         {
            dispatcher.handleWriteResponseException(request, response, e);
            return null;
         }
      }
      finally
      {
         dispatcher.clearContextData();
      }
   }

   protected ModelAndView createModelAndView(ServerResponse serverResponse)
   {
      View view = createView(serverResponse);
      return new ModelAndView(view, "responseInvoker", serverResponse);
   }

   protected View createView(ServerResponse serverResponse)
   {
      String contentType = serverResponse.resolveContentType().toString();
      return new ResteasyView(contentType, dispatcher);
   }

   public boolean supports(Object handler)
   {
      return handler instanceof ResteasyRequestWrapper;
   }

}
