/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpRequestPreprocessor;
import org.jboss.resteasy.util.MediaTypeHelper;

public class AcceptParameterHttpPreprocessor implements HttpRequestPreprocessor
{

   private final String paramMapping;

   public AcceptParameterHttpPreprocessor(String paramMapping)
   {
      if (paramMapping == null || paramMapping.matches("\\s+"))
         throw new IllegalArgumentException("Constructor arg paramMapping is invalid");
      this.paramMapping = paramMapping;
   }

   public void preProcess(HttpRequest request)
   {
      MultivaluedMap<String, String> params = request.getUri().getQueryParameters(false);

      if (params != null)
      {
         List<String> accepts = params.get(paramMapping);

         if (accepts != null && !accepts.isEmpty())
         {
            List<MediaType> mediaTypes = new ArrayList<MediaType>();
            
            for (String accept : accepts)
            {
               try
               {
                  accept = URLDecoder.decode( accept, "UTF-8" );
               }
               catch (UnsupportedEncodingException e)
               {
                  throw new RuntimeException( e );
               }
               mediaTypes.addAll( MediaTypeHelper.parseHeader(accept) );
            }
            
            MediaTypeHelper.sortByWeight(mediaTypes);
            
            request.getHttpHeaders().getAcceptableMediaTypes().addAll(0, mediaTypes);

         }
      }

   }

}
