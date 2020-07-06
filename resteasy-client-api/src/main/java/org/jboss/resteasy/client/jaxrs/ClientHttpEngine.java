package org.jboss.resteasy.client.jaxrs;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

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
   Response invoke(Invocation request);

   default boolean isFollowRedirects() {
      throw new UnsupportedOperationException();
   }

   default void setFollowRedirects(boolean followRedirects) {
      throw new UnsupportedOperationException();
   }

   void close();

}
