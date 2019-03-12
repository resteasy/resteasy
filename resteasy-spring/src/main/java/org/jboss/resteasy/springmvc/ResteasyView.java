package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.core.ServerResponseWriter;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.specimpl.BuiltResponseEntityNotBacked;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
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

@SuppressWarnings("rawtypes")
public class ResteasyView implements View
{

   private MediaType contentType = null;
   private List<MediaType> potentialContentTypes = null;
   private ResteasyDeployment deployment;

   public ResteasyView(final String contentType, final ResteasyDeployment deployment)
   {
      setContentType(contentType);
      this.deployment = deployment;
   }

   public ResteasyView()
   {
   }

   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
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

   public void render(final Map model, final HttpServletRequest servletRequest,
                      final HttpServletResponse servletResponse) throws Exception
   {
      final SynchronousDispatcher dispatcher = (SynchronousDispatcher)deployment.getDispatcher();
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
                  BuiltResponse responseInvoker = getResponse(model, resolvedContentType);
                  if (responseInvoker != null)
                  {
                     ServerResponseWriter.writeNomapResponse(responseInvoker, httpRequest, response, dispatcher.getProviderFactory(), t -> {
                        if(t != null)
                           dispatcher.writeException(httpRequest, response, t, t2 -> {});
                     });
                  }
               }
               catch (Exception e)
               {
                  dispatcher.writeException(httpRequest, response, e, t -> {});
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

   protected BuiltResponse getResponse(Map model, MediaType mt)
   {
      Collection modelValues = model.values();
      for (Object value : modelValues)
      {
         if (value instanceof BuiltResponse)
         {
            return (BuiltResponse) value;
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

   private BuiltResponse createResponse(Object value, MediaType contentType)
   {
      BuiltResponseEntityNotBacked responseImpl = new BuiltResponseEntityNotBacked();
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
