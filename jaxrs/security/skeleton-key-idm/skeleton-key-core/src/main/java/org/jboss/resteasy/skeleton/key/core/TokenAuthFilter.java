package org.jboss.resteasy.skeleton.key.core;

import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.jboss.resteasy.skeleton.key.client.TokenFactory;
import org.jboss.resteasy.skeleton.key.client.TokenVerifier;
import org.jboss.resteasy.skeleton.key.keystone.model.Access;
import org.jboss.resteasy.skeleton.key.keystone.model.Role;
import org.jboss.resteasy.skeleton.key.server.TokenService;
import org.jboss.resteasy.spi.UnauthorizedException;
import sun.security.x509.X509Cert;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

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
