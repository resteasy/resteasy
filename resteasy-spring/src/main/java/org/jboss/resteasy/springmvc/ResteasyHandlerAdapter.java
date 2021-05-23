package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.ServerResponseWriter;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.UnhandledException;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletionException;

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
   protected ResteasyDeployment deployment;

   public ResteasyHandlerAdapter(final ResteasyDeployment deployment)
   {
      super(deployment.getProviderFactory());
      this.deployment = deployment;
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
      SynchronousDispatcher dispatcher = (SynchronousDispatcher)deployment.getDispatcher();
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
      if (requestWrapper.getAbortedResponse() != null)
      {
         ServerResponseWriter.writeNomapResponse(((BuiltResponse) requestWrapper.getAbortedResponse()), requestWrapper.getHttpRequest(), response, dispatcher.getProviderFactory(), t -> {});
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
      SynchronousDispatcher dispatcher = (SynchronousDispatcher)deployment.getDispatcher();
      dispatcher.pushContextObjects(request, response);
      try
      {
         BuiltResponse jaxrsResponse = null;
         try
         {
            jaxrsResponse = (BuiltResponse) requestWrapper.getInvoker().invoke(request, response);
         }
         catch (CompletionException e)
         {
            dispatcher.writeException(request, response, e.getCause(), t -> {});
         }
         catch (Exception e)
         {
            dispatcher.writeException(request, response, e, t -> {});
         }

         if (jaxrsResponse == null)
            return null;

         try
         {
            Object entity = jaxrsResponse.getEntity();
            if (entity instanceof ModelAndView)
            {
               ServerResponseWriter.commitHeaders(jaxrsResponse, response);
               return (ModelAndView) entity;
            }
            return createModelAndView(jaxrsResponse);
         }
         catch (Exception e)
         {
            dispatcher.writeException(request, response, e, t -> {});
            return null;
         }
      }
      finally
      {
         dispatcher.clearContextData();
      }
   }

   protected ModelAndView createModelAndView(BuiltResponse serverResponse)
   {
      View view = createView(serverResponse);
      return new ModelAndView(view, "responseInvoker", serverResponse);
   }

   protected View createView(BuiltResponse serverResponse)
   {
      String contentType = ServerResponseWriter.resolveContentType(serverResponse).toString();
      return new ResteasyView(contentType, deployment);
   }

   public boolean supports(Object handler)
   {
      return handler instanceof ResteasyRequestWrapper;
   }

}
