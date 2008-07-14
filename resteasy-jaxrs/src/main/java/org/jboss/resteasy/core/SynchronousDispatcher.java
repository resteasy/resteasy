package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SynchronousDispatcher implements Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected ResourceMethodRegistry registry;
   protected Map<String, MediaType> mediaTypeMappings;
   protected Map<String, String> languageMappings;
   private final static Logger logger = LoggerFactory.getLogger(SynchronousDispatcher.class);

   public SynchronousDispatcher()
   {
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setMediaTypeMappings(Map<String, MediaType> mediaTypeMappings)
   {
      this.mediaTypeMappings = mediaTypeMappings;
   }

   public void setLanguageMappings(Map<String, String> languageMappings)
   {
      this.languageMappings = languageMappings;
   }

   public Map<String, MediaType> getMediaTypeMappings()
   {
      return mediaTypeMappings;
   }

   public Map<String, String> getLanguageMappings()
   {
      return languageMappings;
   }

   public void preprocess(HttpRequest in)
   {
      if (mediaTypeMappings == null && languageMappings == null) return;
      List<PathSegment> segments = in.getUri().getPathSegments(false);
      PathSegment last = segments.get(segments.size() - 1);
      int index;
      if ((index = last.getPath().indexOf('.')) == -1) return;
      String extension = last.getPath().substring(index + 1);
      String[] extensions = extension.split("\\.");

      boolean preprocessed = false;

      String rebuilt = last.getPath().substring(0, index);
      for (String ext : extensions)
      {
         if (mediaTypeMappings != null)
         {
            MediaType match = mediaTypeMappings.get(ext);
            if (match != null)
            {
               in.getHttpHeaders().getAcceptableMediaTypes().add(match);
               preprocessed = true;
               continue;
            }
         }
         if (languageMappings != null)
         {
            String match = languageMappings.get(ext);
            if (match != null)
            {
               in.getHttpHeaders().getAcceptableLanguages().add(match);
               preprocessed = true;
               continue;
            }
         }
         rebuilt += "." + ext;
      }
      if (!preprocessed) return;
      @SuppressWarnings("unused")
      String newPath = last.getPath().substring(0, index) + rebuilt;

      List<PathSegment> newSegments = new ArrayList<PathSegment>(segments.size());
      for (PathSegment segment : segments)
      {
         newSegments.add(segment);
      }

      PathSegmentImpl newSegment = new PathSegmentImpl(rebuilt, last.getMatrixParameters());
      newSegments.set(newSegments.size() - 1, newSegment);
      in.setPreProcessedSegments(newSegments);
   }

   public void invoke(HttpRequest in, HttpResponse response)
   {
      logger.debug("PathInfo: " + in.getUri().getPath());
      preprocess(in);
      ResourceInvoker invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(in, response);
      }
      catch (Failure e)
      {
         try
         {
            response.sendError(e.getErrorCode());
         }
         catch (IOException e1)
         {
            throw new RuntimeException(e1);
         }
         e.printStackTrace();
         return;
      }
      if (invoker == null)
      {
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }
      invoke(in, response, invoker);
   }

   public void invoke(HttpRequest in, HttpResponse response, ResourceInvoker invoker)
   {
      try
      {
         ResteasyProviderFactory.pushContext(HttpRequest.class, in);
         ResteasyProviderFactory.pushContext(HttpResponse.class, response);
         ResteasyProviderFactory.pushContext(HttpHeaders.class, in.getHttpHeaders());
         ResteasyProviderFactory.pushContext(UriInfo.class, in.getUri());
         ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(in.getHttpHeaders(), in.getHttpMethod()));
         try
         {
            invoker.invoke(in, response);
         }
         catch (Failure e)
         {
            try
            {
               response.sendError(e.getErrorCode());
            }
            catch (IOException e1)
            {
               throw new RuntimeException(e1);
            }
            e.printStackTrace();
            return;
         }

      }
      catch (Exception e)
      {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         e.printStackTrace();
         return;
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
   }

}