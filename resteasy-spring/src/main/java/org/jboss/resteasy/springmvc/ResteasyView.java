package org.jboss.resteasy.springmvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.specimpl.ResponseImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.View;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/

public class ResteasyView implements View
{

   private String contentType = null;
   private List<String> potentialContentTypes = null;
   private SynchronousDispatcher dispatcher;

   public ResteasyView(String contentType, SynchronousDispatcher dispatcher)
   {
      setContentType(contentType);
      setDispatcher(dispatcher);
   }

   public ResteasyView()
   {
   }

   public SynchronousDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setDispatcher(SynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public String getContentType()
   {
      return contentType;
   }

   public List<String> getPotentialContentTypes()
   {
      return potentialContentTypes;
   }

   public void setPotentialContentTypes(List<String> potentialContentTypes)
   {
      this.potentialContentTypes = potentialContentTypes;
   }

   @SuppressWarnings("unchecked")
   public void render(Map model, HttpServletRequest servletRequest,
         HttpServletResponse servletResponse) throws Exception
   {
      HttpRequest request = RequestUtil.getHttpRequest(servletRequest);
      HttpResponse response = new HttpServletResponseWrapper(
            servletResponse, dispatcher.getProviderFactory());

      try
      {
         ResteasyProviderFactory.pushContext(HttpServletRequest.class,
               servletRequest);
         ResteasyProviderFactory.pushContext(HttpServletResponse.class,
               servletResponse);
         ResteasyProviderFactory.pushContext(SecurityContext.class,
               new ServletSecurityContext(servletRequest));

         dispatcher.getDispatcherUtilities().pushContextObjects(request, response);

         writeResponse(request, response, getResponse(model));

      }
      catch (Exception e)
      {
         dispatcher.handleWriteResponseException(request, response, e);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
   }

   private void writeResponse(HttpRequest jaxrsRequest, HttpResponse response,
         ResponseInvoker responseInvoker) throws IOException
   {
      if (responseInvoker == null)
      {
         return;
      }

      MediaType unresolvedType = responseInvoker.getContentType();
      responseInvoker.setContentType(resolveContentType(jaxrsRequest,
            unresolvedType));

      if (responseInvoker.getWriter() == null)
      {
         String message = "Could not find MessageBodyWriter for response object of type: %s of media type: %s";
         throw new LoggableFailure(String.format(message, responseInvoker
               .getType().getName(), unresolvedType),
               HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      }
      responseInvoker.writeTo(response);
   }

   private MediaType resolveContentType(HttpRequest jaxrsRequest, MediaType mt)
   {
      if (MediaType.MEDIA_TYPE_WILDCARD.equals(mt.getType())
            && !CollectionUtils.isEmpty(potentialContentTypes))
      {

         List<MediaType> acceptableMediaTypes = jaxrsRequest.getHttpHeaders()
               .getAcceptableMediaTypes();

         for (String potentialContentTypesStr : potentialContentTypes)
         {
            MediaType potentialContentType = MediaType
                  .valueOf(potentialContentTypesStr);
            for (MediaType acceptableMediaType : acceptableMediaTypes)
            {
               if (acceptableMediaType.isCompatible(potentialContentType))
               {
                  return potentialContentType;
               }
            }
         }
      }
      return mt;
   }

   @SuppressWarnings("unchecked")
   protected ResponseInvoker getResponse(Map model)
   {
      for (Object value : model.values())
      {
         if (value instanceof ResponseInvoker)
         {
            return (ResponseInvoker) value;
         }
      }
      if (model.size() == 1)
      {
         ResponseImpl responseImpl = new ResponseImpl();
         responseImpl.setEntity(model.values().iterator().next());
         return new ResponseInvoker(dispatcher.getDispatcherUtilities(), responseImpl);
      }
      return null;
   }


   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

}
