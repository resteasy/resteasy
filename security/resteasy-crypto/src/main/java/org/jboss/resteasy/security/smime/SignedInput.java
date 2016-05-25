package org.jboss.resteasy.security.smime;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.ws.rs.core.GenericType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface SignedInput<T>
{
   T getEntity();
   <T2> T2 getEntity(Class<T2> type);
   Object getEntity(GenericType type);

   boolean verify() throws Exception;

   boolean verify(X509Certificate certificate) throws Exception;

   boolean verify(PublicKey publicKey) throws Exception;
}
