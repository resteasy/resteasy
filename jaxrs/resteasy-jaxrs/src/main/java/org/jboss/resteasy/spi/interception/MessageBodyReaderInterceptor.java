package org.jboss.resteasy.spi.interception;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * Wraps around invocations of MessageBodyReader.readFrom().
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MessageBodyReaderInterceptor
{
   /**
    *
    * @param context
    * @return the object read
    * @throws IOException
    * @throws WebApplicationException
    */
   Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException;

}