package org.resteasy.spi;

import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;

/**
 * Bridge interface between the base Resteasy JAX-RS implementation and the actual HTTP transport (i.e. a servlet container)
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HttpOutput
{
   MultivaluedMap<String, Object> getOutputHeaders();

   OutputStream getOutputStream();
}
