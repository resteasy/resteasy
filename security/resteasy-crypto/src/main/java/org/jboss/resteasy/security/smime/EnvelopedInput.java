package org.jboss.resteasy.security.smime;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.ws.rs.core.GenericType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public interface EnvelopedInput<T>
{
   T getEntity();
   T getEntity(PrivateKey key, X509Certificate cert);
   <T2> T2 getEntity(Class<T2> type);
   <T2> T2 getEntity(Class<T2> type, PrivateKey key, X509Certificate cert);
   Object getEntity(GenericType type);
   Object getEntity(GenericType type, PrivateKey key, X509Certificate cert);
}
