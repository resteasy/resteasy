package org.jboss.resteasy.spi;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.List;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpRequest
{
   HttpHeaders getHttpHeaders();

   InputStream getInputStream();

   UriInfo getUri();

   String getHttpMethod();

   /**
    * Path segments minus any preprocessed extensions
    *
    * @return
    */
   List<PathSegment> getPreProcessedSegments();


   void setPreProcessedSegments(List<PathSegment> segments);
}
