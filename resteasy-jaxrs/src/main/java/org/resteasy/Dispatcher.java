package org.resteasy;

import org.resteasy.specimpl.PathSegmentImpl;
import org.resteasy.specimpl.RequestImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

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
public class Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected ResourceMethodRegistry registry;
   protected Map<String, MediaType> mimeExtensions;
   protected Map<String, String> languageExtensions;

   public Dispatcher(ResteasyProviderFactory providerFactory)
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

   public void setMimeExtensions(Map<String, MediaType> mimeExtensions)
   {
      this.mimeExtensions = mimeExtensions;
   }

   public void setLanguageExtensions(Map<String, String> languageExtensions)
   {
      this.languageExtensions = languageExtensions;
   }

   protected void preprocess(HttpRequest in)
   {
      if (mimeExtensions == null && languageExtensions == null) return;
      List<PathSegment> segments = in.getUri().getPathSegments();
      PathSegment last = segments.get(segments.size() - 1);
      int index;
      if ((index = last.getPath().indexOf('.')) == -1) return;
      String extension = last.getPath().substring(index + 1);
      String[] extensions = extension.split("\\.");

      boolean preprocessed = false;

      String rebuilt = last.getPath().substring(0, index);
      for (String ext : extensions)
      {
         if (mimeExtensions != null)
         {
            MediaType match = mimeExtensions.get(ext);
            if (match != null)
            {
               in.getHttpHeaders().getAcceptableMediaTypes().add(match);
               preprocessed = true;
               continue;
            }
         }
         if (languageExtensions != null)
         {
            String match = languageExtensions.get(ext);
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

   public static void main(String[] args) throws Exception
   {
      String[] split = "xml.en".split("\\.");
      System.out.println(split.length);
   }

}
