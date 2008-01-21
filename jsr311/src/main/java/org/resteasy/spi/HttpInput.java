package org.resteasy.spi;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpInput
{
   HttpHeaders getHttpHeaders();

   InputStream getInputStream();

   UriInfo getUri();

   MultivaluedMap<String, String> getParameters();
}
