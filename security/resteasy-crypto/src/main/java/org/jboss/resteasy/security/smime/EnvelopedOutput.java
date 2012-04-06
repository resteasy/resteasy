package org.jboss.resteasy.security.smime;

import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EnvelopedOutput extends SMIMEOutput
{
   public EnvelopedOutput(Object obj, String mediaType)
   {
      super(obj, mediaType);
   }

   public EnvelopedOutput(Object obj, MediaType mediaType)
   {
      super(obj, mediaType);
   }
}
