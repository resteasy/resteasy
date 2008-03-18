package org.resteasy.spi;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpInput
{
   HttpHeaders getHttpHeaders();

   InputStream getInputStream();

   UriInfo getUri();

   MultivaluedMap<String, String> getParameters();

   String getHttpMethod();
}
