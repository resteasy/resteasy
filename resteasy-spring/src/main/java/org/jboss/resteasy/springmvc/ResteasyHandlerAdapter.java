package org.jboss.resteasy.springmvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.DispatcherUtilities;
import org.jboss.resteasy.core.NoMessageBodyWriterFoundFailure;
import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.UnhandledException;
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
public class ResteasyHandlerAdapter extends ResteasyWebHandlerTemplate<ModelAndView> implements HandlerAdapter
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
      if( requestWrapper.getErrorCode() != null )
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
      if( dispatcher instanceof AsynchronousDispatcher )
      {
         AsynchronousDispatcher asyncDispatcher = (AsynchronousDispatcher)dispatcher;
         if(asyncDispatcher.isAsynchrnousRequest(request))
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
      dispatcher.getDispatcherUtilities().pushContextObjects(request,
            response);

      Response jaxrsResponse = null;
      try
      {
         jaxrsResponse = requestWrapper.getInvoker().invoke(request, response);
      }
      catch (Exception e)
      {
         dispatcher.handleInvokerException(request, response, e);
      }

      try
      {
         if (jaxrsResponse != null)
         {
            ResponseInvoker responseInvoker = writeHeaders(response, jaxrsResponse);
            if (responseInvoker == null)
               return null;
            if (jaxrsResponse.getEntity() instanceof ModelAndView)
               return (ModelAndView) jaxrsResponse.getEntity();
            else
               return createModelAndView(responseInvoker);
         }
      }
      catch (Exception e)
      {
         dispatcher.handleWriteResponseException(request, response, e);
      }
      return null;
   }

   private ResponseInvoker writeHeaders(HttpResponse response,
         Response jaxrsResponse)
   {
      DispatcherUtilities dispatcherUtilities = dispatcher
            .getDispatcherUtilities();
      ResponseInvoker responseInvoker = null;
      try
      {
         responseInvoker = dispatcherUtilities.writeHeaders(response, jaxrsResponse);
      } 
      catch( NoMessageBodyWriterFoundFailure e)
      {
         if(e.getResponseInvoker().getEntity() instanceof ModelAndView)
         {
            responseInvoker = e.getResponseInvoker();
            dispatcherUtilities.outputHeaders(response, jaxrsResponse);
         }
      }
      return responseInvoker;
   }

   protected ModelAndView createModelAndView(ResponseInvoker responseInvoker)
   {
      return new ModelAndView(createView(responseInvoker))
            .addObject("responseInvoker", responseInvoker);
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
