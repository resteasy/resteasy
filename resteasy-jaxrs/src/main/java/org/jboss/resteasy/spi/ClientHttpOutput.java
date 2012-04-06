package org.jboss.resteasy.spi;

import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;

/**
 * Bridge interface between the base Resteasy JAX-RS client implementation and the actual HTTP transport (i.e. Apache HTTP Client, java.net.HttpURLConnection, etc...)
 * <p/>
 * This object is filled with request data before it is sent to the server.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientHttpOutput
{
   MultivaluedMap<String, Object> getOutputHeaders();

   OutputStream getOutputStream();

}
