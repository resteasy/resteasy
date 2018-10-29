package org.jboss.resteasy.security.smime;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EnvelopedOutput extends SMIMEOutput
{
   public EnvelopedOutput(final Object obj, final String mediaType)
   {
      super(obj, mediaType);
   }

   public EnvelopedOutput(final Object obj, final MediaType mediaType)
   {
      super(obj, mediaType);
   }
}
