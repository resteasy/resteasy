package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileRangeException extends WebApplicationException
{
   public FileRangeException(MediaType mediaType, File file, long begin, long end)
   {
      super(Response.status(206).entity(new FileRange(file, begin, end)).type(mediaType).build());
   }
}
