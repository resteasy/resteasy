package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.PathSegmentImpl;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modifies Accept and Accept-Language headers by looking at path file suffix i.e. .xml means Accept application/xml
 */
@Provider
@PreMatching
public class AcceptHeaderByFileSuffixFilter implements ContainerRequestFilter
{
   private final Map<String, String> mediaTypeMappings = new HashMap<String, String>();
   private final Map<String, String> languageMappings = new HashMap<String, String>();

   public void setMediaTypeMappings(Map<String, MediaType> mediaTypeMappings)
   {
      this.mediaTypeMappings.clear();
      for (Map.Entry<String, MediaType> entry : mediaTypeMappings.entrySet())
      {
         this.mediaTypeMappings.put(entry.getKey(), entry.getValue().toString());
      }
   }

   public void setLanguageMappings(Map<String, String> languageMappings)
   {
      this.languageMappings.clear();
      for (Map.Entry<String, String> entry : languageMappings.entrySet())
      {
         this.languageMappings.put(entry.getKey(), entry.getValue());
      }
   }

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      List<PathSegment> segments = null;
      if (mediaTypeMappings != null || languageMappings != null)
      {
         segments = process(requestContext, segments);
      }
      if (segments == null)
      {
         return;
      }

      StringBuilder preprocessedPath = new StringBuilder();
      for (PathSegment pathSegment : segments)
      {
         preprocessedPath.append("/").append(pathSegment.getPath());
      }
      if (! requestContext.getUriInfo().getQueryParameters().isEmpty())
      {
         char sep = '?';
         for (Map.Entry<String, List<String>> entry : requestContext.getUriInfo().getQueryParameters(false).entrySet()) {
            for (String value : entry.getValue()) {
               preprocessedPath.append(sep);
               sep = '&';
               preprocessedPath.append(entry.getKey()).append('=').append(value);
            }
         }
      }
      URI requestUri = URI.create(preprocessedPath.toString());
      requestContext.setRequestUri(requestUri);

   }

   private List<PathSegment> process(ContainerRequestContext in, List<PathSegment> segments)
   {
      String path = in.getUriInfo().getPath(false);
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
            String match = mediaTypeMappings.get(ext);
            if (match != null)
            {
               in.getHeaders().addFirst(HttpHeaders.ACCEPT, match);
               preprocessed = true;
               continue;
            }
         }
         if (languageMappings != null)
         {
            String match = languageMappings.get(ext);
            if (match != null)
            {
               in.getHeaders().add(HttpHeaders.ACCEPT_LANGUAGE, match);
               preprocessed = true;
               continue;
            }
         }
         rebuilt.append(".").append(ext);
      }
      if (preprocessed)
      {
         segments = PathSegmentImpl.parseSegments(rebuilt.toString(), false);
      }
      return segments;
   }

}
