package org.jboss.resteasy.plugins.providers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileRangeException extends WebApplicationException
{
   private static final long serialVersionUID = -5615796352743435769L;

   public FileRangeException(final MediaType mediaType, final File file, final long begin, final long end)
   {
      super(Response.status(206).entity(new FileRange(file, begin, end)).type(mediaType).build());
   }
}
