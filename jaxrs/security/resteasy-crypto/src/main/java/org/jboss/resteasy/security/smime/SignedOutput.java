package org.jboss.resteasy.security.smime;

import javax.ws.rs.core.MediaType;
import java.security.PrivateKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SignedOutput extends SMIMEOutput
{
   protected PrivateKey privateKey;

   public SignedOutput(Object obj, String mediaType)
   {
      super(obj, mediaType);
   }

   public SignedOutput(Object obj, MediaType mediaType)
   {
      super(obj, mediaType);
   }

   public PrivateKey getPrivateKey()
   {
      return privateKey;
   }

   public void setPrivateKey(PrivateKey privateKey)
   {
      this.privateKey = privateKey;
   }
}
