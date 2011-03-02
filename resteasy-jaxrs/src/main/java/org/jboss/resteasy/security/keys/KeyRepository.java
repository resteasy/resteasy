package org.jboss.resteasy.security.keys;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface KeyRepository
{
   public PrivateKey getPrivateKey(String identity);

   public PublicKey getPublicKey(String identity);
}
