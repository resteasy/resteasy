package org.jboss.resteasy.core.interception;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MessageBodyWriterInterceptor
{
   void write(MessageBodyWriterContext context) throws IOException, WebApplicationException;

}
