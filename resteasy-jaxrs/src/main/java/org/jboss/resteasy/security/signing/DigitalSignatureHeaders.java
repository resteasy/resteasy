package org.jboss.resteasy.security.signing;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface DigitalSignatureHeaders
{
   public static final String SIGNATURE = "X-RHT-Signature";
   public static final String TIMESTAMP = "X-RHT-Signature-Timestamp";
   public static final String ALGORITHM = "X-RHT-Signature-Algorithm";
   public static final String SIGNER = "X-RHT-Signer";

}
