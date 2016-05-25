package org.jboss.resteasy.keystone.core;

import org.jboss.resteasy.keystone.client.TokenVerifier;
import org.jboss.resteasy.keystone.model.Access;

import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.security.cert.X509Certificate;

/**
 * Server filter that can verify a token id with skeleton key server, or verify signature of a signed token.
 *
 * If there is no X-Auth-Token header, then this filter just returns expecting the application to do role checking
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@PreMatching
@Provider
public class TokenAuthFilter extends AbstractTokenAuthFilter
{
   protected TokenVerifier tokenVerifier;

   public TokenAuthFilter(X509Certificate certificate, TokenVerifier tokenVerifier)
   {
      super(certificate);
      this.tokenVerifier = tokenVerifier;
   }

   @Override
   protected Access getTokenFromServer(String header)
   {
      try
      {
         return tokenVerifier.get(header);
      }
      catch (Exception e)
      {
         return null;
      }
   }

}
