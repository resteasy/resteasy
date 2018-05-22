package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientHttpEngine
{
   /**
    * Needed for Client.getSslContext();
    * @return {@link SSLContext}
    */
   SSLContext getSslContext();

   /**
    * Needed for Client.getHostnameVerifier()
    *
    * @return {@link HostnameVerifier}
    */
   HostnameVerifier getHostnameVerifier();
   ClientResponse invoke(ClientInvocation request);
   void close();

}
