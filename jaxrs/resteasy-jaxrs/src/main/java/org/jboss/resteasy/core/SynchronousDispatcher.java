package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.RequestImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.LocaleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
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
      preprocessExtensions(in);
   }

   protected void preprocessExtensions(HttpRequest in)
   {

      List<PathSegment> segments = null;
      if (mediaTypeMappings != null || languageMappings != null)
      {

         String path = in.getUri().getPath(false);
         int lastSegment = path.lastIndexOf('/');
         if (lastSegment < 0) lastSegment = 0;
         int index = path.indexOf('.', lastSegment);
         if (index < 0) return;

         boolean preprocessed = false;

         String extension = path.substring(index + 1);
         String[] extensions = extension.split("\\.");

         String rebuilt = path.substring(0, index);
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
                  in.getHttpHeaders().getAcceptableLanguages().add(LocaleHelper.extractLocale(match));
                  preprocessed = true;
                  continue;
               }
            }
            rebuilt += "." + ext;
         }
         if (preprocessed) segments = PathSegmentImpl.parseSegments(rebuilt);
         else segments = in.getUri().getPathSegments(false);
      }
      else
      {
         segments = in.getUri().getPathSegments(false);
      }

      // finally strip out matrix parameters

      StringBuffer preprocessedPath = new StringBuffer();
      for (PathSegment pathSegment : segments)
      {
         preprocessedPath.append("/").append(pathSegment.getPath());
      }
      in.setPreprocessedPath(preprocessedPath.toString());
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
         logger.debug("Could not match path: " + in.getUri().getPath(), e);
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
         logger.debug("Could not match path: " + in.getUri().getPath());
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
         ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(in));
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
            logger.error("Failure in processing: " + in.getHttpMethod() + " " + in.getUri().getPath(), e);
            return;
         }

      }
      catch (Exception e)
      {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         logger.error("Failure in processing: " + in.getHttpMethod() + " " + in.getUri().getPath(), e);
         return;
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
   }

}