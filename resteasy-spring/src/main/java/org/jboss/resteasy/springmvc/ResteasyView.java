package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
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
      ResteasyWebHandlerTemplate template = new ResteasyWebHandlerTemplate<Void>(dispatcher.getProviderFactory())
      {
         protected Void handle(ResteasyRequestWrapper requestWrapper,
                               HttpResponse response) throws Exception
         {
            HttpRequest httpRequest = requestWrapper.getHttpRequest();
            dispatcher.pushContextObjects(httpRequest, response);
            try
            {
               try
               {
                  MediaType resolvedContentType = resolveContentType(httpRequest,
                          httpRequest.getHttpHeaders().getMediaType());
                  ServerResponse responseInvoker = getResponse(model, resolvedContentType);
                  if (responseInvoker != null)
                  {
                     responseInvoker.writeTo(httpRequest, response, dispatcher.getProviderFactory());
                  }
               }
               catch (Exception e)
               {
                  dispatcher.handleWriteResponseException(httpRequest, response, e);
               }
               return null;
            }
            finally
            {
               dispatcher.clearContextData();
            }
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

         if (contentType != null && isAcceptable(acceptableMediaTypes, contentType))
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
            isAcceptable = true;
            break;
         }
      }
      return isAcceptable;
   }

   @SuppressWarnings("unchecked")
   protected ServerResponse getResponse(Map model, MediaType mt)
   {
      Collection modelValues = model.values();
      for (Object value : modelValues)
      {
         if (value instanceof ServerResponse)
         {
            return (ServerResponse) value;
         }
      }

      if (model.size() == 1)
      {
         return createResponse(modelValues.iterator().next(), mt);
      }
      if (model.size() == 2)
      {
         for (Object value : modelValues)
         {
            if (!(value instanceof BindingResult))
            {
               return createResponse(value, mt);
            }
         }
      }
      return null;
   }

   private ServerResponse createResponse(Object value, MediaType contentType)
   {
      ServerResponse responseImpl = new ServerResponse();
      responseImpl.setEntity(value);
      if (contentType != null)
         responseImpl.getMetadata().putSingle(HttpHeaderNames.CONTENT_TYPE, contentType);
      return responseImpl;
   }

   public void setContentType(String contentType)
   {
      this.contentType = MediaType.valueOf(contentType);
   }

}
