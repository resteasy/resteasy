package org.jboss.resteasy.security.doseta;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface KeyRepository
{
   PrivateKey findPrivateKey(DKIMSignature header);

   PublicKey findPublicKey(DKIMSignature header);

   /**
    * What should be the default domain to use when creating signature header
    *
    * @return null if none
    */
   String getDefaultPrivateDomain();

   /**
    * What should be the default selector to use when creating signature header
    *
    * @return null if none
    */
   String getDefaultPrivateSelector();
}
