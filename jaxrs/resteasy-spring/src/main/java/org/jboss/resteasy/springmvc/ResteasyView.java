package org.jboss.resteasy.springmvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResponseInvoker;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResponseImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;

/**
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class ResteasyView implements View
{

   private MediaType contentType = null;
   private List<MediaType> potentialContentTypes = null;
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
      return contentType.toString();
   }

   public List<String> getPotentialContentTypes()
   {
      List<String> contentTypes = new ArrayList<String>(potentialContentTypes.size());
      for (MediaType mediaType : potentialContentTypes)
      {
         contentTypes.add(mediaType.toString());
      }
      return contentTypes;
   }

   public void setPotentialContentTypes(List<String> potentialContentTypes)
   {
      this.potentialContentTypes = new ArrayList<MediaType>();
      for (String type : potentialContentTypes)
      {
         this.potentialContentTypes.add(MediaType.valueOf(type));
      }
   }

   @SuppressWarnings("unchecked")
   public void render(final Map model, final HttpServletRequest servletRequest,
         final HttpServletResponse servletResponse) throws Exception
   {
      ResteasyWebHandlerTemplate template = new ResteasyWebHandlerTemplate<Void>(dispatcher){
         protected Void handle(ResteasyRequestWrapper requestWrapper,
               HttpResponse response) throws Exception
         {
            HttpRequest httpRequest = requestWrapper.getHttpRequest();
            try
            {
               MediaType resolvedContentType = resolveContentType(httpRequest,
                     httpRequest.getHttpHeaders().getMediaType());
               if(resolvedContentType != null )
                  servletResponse.setContentType(resolvedContentType.toString());
               ResponseInvoker responseInvoker = getResponse(model, resolvedContentType);
               if (responseInvoker != null)
               {
                  
                  if (responseInvoker.getWriter() == null)
                  {
                     String message = "Could not find MessageBodyWriter for response object of type: %s of media type: %s";
                     throw new LoggableFailure(String.format(message, responseInvoker
                           .getType().getName(), resolvedContentType),
                           HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
                  }
                  responseInvoker.writeTo(response);
               }
            } 
            catch (Exception e)
            {
               dispatcher.handleWriteResponseException(httpRequest, response, e);
            }
            return null;
         }
      };
      ResteasyRequestWrapper requestWrapper = RequestUtil.getRequestWrapper(servletRequest);
      template.handle(requestWrapper, servletResponse);
   }

   private MediaType resolveContentType(HttpRequest jaxrsRequest, MediaType mt)
   {
      if (mt == null || MediaType.MEDIA_TYPE_WILDCARD.equals(mt.getType()))
      {
         List<MediaType> acceptableMediaTypes = jaxrsRequest.getHttpHeaders()
               .getAcceptableMediaTypes();
         
         if(contentType != null && isAcceptable(acceptableMediaTypes, contentType))
         {
            return contentType;
         }

         return MediaTypeHelper.getBestMatch(potentialContentTypes, acceptableMediaTypes);
      }
      return mt;
   }

   private boolean isAcceptable(List<MediaType> acceptableMediaTypes,
         MediaType potentialContentType)
   {
      boolean isAcceptable = false;
      for (MediaType acceptableMediaType : acceptableMediaTypes)
      {
         if (acceptableMediaType.isCompatible(potentialContentType))
         {
            isAcceptable=true;
            break;
         }
      }
      return isAcceptable;
   }

   @SuppressWarnings("unchecked")
   protected ResponseInvoker getResponse(Map model, MediaType mt)
   {
      Collection modelValues = model.values();
      for (Object value : modelValues)
      {
         if (value instanceof ResponseInvoker)
         {
            return (ResponseInvoker) value;
         }
      }
      
      if (model.size() == 1)
      {
         return createResponseInvoker(modelValues.iterator().next(), mt);
      }
      if (model.size() == 2)
      {
         for (Object value : modelValues)
         {
            if (!(value instanceof BindingResult))
            {
               return createResponseInvoker(value, mt);
            }
         }
      }
      return null;
   }

   private ResponseInvoker createResponseInvoker(Object value, MediaType contentType)
   {
      ResponseImpl responseImpl = new ResponseImpl();
      responseImpl.setEntity(value);
      if( contentType != null )
         responseImpl.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, contentType);
      return dispatcher.getDispatcherUtilities().createResponseInvoker(responseImpl);
   }

   public void setContentType(String contentType)
   {
      this.contentType = MediaType.valueOf(contentType);
   }

}
