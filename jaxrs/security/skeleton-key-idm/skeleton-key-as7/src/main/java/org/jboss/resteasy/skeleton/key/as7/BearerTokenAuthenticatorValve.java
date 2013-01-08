package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.AbstractClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.skeleton.key.EnvUtil;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeySession;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.as7.config.RemoteSkeletonKeyConfig;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Map;

/**
 * Uses a configured remote auth server to do Bearer token authentication only.  SkeletonKeyTokens are used
 * to provide user data and role mappings.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BearerTokenAuthenticatorValve extends AbstractRemoteOAuthAuthenticatorValve
{
   private static final Logger log = Logger.getLogger(BearerTokenAuthenticatorValve.class);


   @Override
   public void invoke(Request request, Response response) throws IOException, ServletException
   {
      try
      {
         super.invoke(request, response);
      }
      finally
      {
         ResteasyProviderFactory.clearContextData(); // to clear push of SkeletonKeySession
      }
   }

   @Override
   protected boolean authenticate(Request request, HttpServletResponse response, LoginConfig config) throws IOException
   {
      CatalinaBearerTokenAuthenticator bearer = new CatalinaBearerTokenAuthenticator(true, resourceMetadata);
      try
      {
         if (bearer.login(request, response))
         {
            SkeletonKeyTokenVerification verification = bearer.getVerification();
            Principal principal = new CatalinaSecurityContextHelper().createPrincipal(context.getRealm(), verification.getPrincipal(), verification.getRoles());
            request.setUserPrincipal(principal);
            if (!remoteSkeletonKeyConfig.isCancelPropagation())
            {
               SkeletonKeySession skSession = new SkeletonKeySession(verification.getPrincipal().getToken(), resourceMetadata);
               request.setAttribute(SkeletonKeySession.class.getName(), skSession);
               ResteasyProviderFactory.pushContext(SkeletonKeySession.class, skSession);
            }
            return true;
         }
      }
      catch (LoginException e)
      {
         return false;
      }
      return false;
   }
}
