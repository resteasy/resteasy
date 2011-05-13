package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.util.LocaleHelper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import java.util.List;
import java.util.Map;

public class ExtensionHttpPreprocessor implements HttpRequestPreprocessor
{
   public Map<String, MediaType> mediaTypeMappings;
   public Map<String, String> languageMappings;

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

   public void preProcess(HttpRequest in)
   {
      List<PathSegment> segments = null;
      if (mediaTypeMappings != null || languageMappings != null)
      {
         segments = process(in, segments);
      }
      if (segments == null)
      {
         segments = in.getUri().getPathSegments(false);
      }

      // finally strip out matrix parameters

      StringBuilder preprocessedPath = new StringBuilder();
      for (PathSegment pathSegment : segments)
      {
         preprocessedPath.append("/").append(pathSegment.getPath());
      }
      in.setPreprocessedPath(preprocessedPath.toString());
   }

   private List<PathSegment> process(HttpRequest in, List<PathSegment> segments)
   {
      String path = in.getUri().getPath(false);
      int lastSegment = path.lastIndexOf('/');
      if (lastSegment < 0)
      {
         lastSegment = 0;
      }
      int index = path.indexOf('.', lastSegment);
      if (index < 0)
      {
         return null;
      }

      boolean preprocessed = false;

      String extension = path.substring(index + 1);
      String[] extensions = extension.split("\\.");

      StringBuilder rebuilt = new StringBuilder(path.substring(0, index));
      for (String ext : extensions)
      {
         if (mediaTypeMappings != null)
         {
            MediaType match = mediaTypeMappings.get(ext);
            if (match != null)
            {
               in.getHttpHeaders().getAcceptableMediaTypes().add(0, match);
               preprocessed = true;
               continue;
            }
         }
         if (languageMappings != null)
         {
            String match = languageMappings.get(ext);
            if (match != null)
            {
               in.getHttpHeaders().getAcceptableLanguages().add(
                       LocaleHelper.extractLocale(match));
               preprocessed = true;
               continue;
            }
         }
         rebuilt.append(".").append(ext);
      }
      if (preprocessed)
      {
         segments = PathSegmentImpl.parseSegments(rebuilt.toString());
      }
      return segments;
   }

}
