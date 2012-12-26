package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.jboss.logging.Logger;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CatalinaBearerTokenAuthenticator
{
   protected ResourceMetadata resourceMetadata;
   protected SkeletonKeyTokenVerification verification;
   protected boolean challenge;
   protected Logger log = Logger.getLogger(CatalinaBearerTokenAuthenticator.class);

   public CatalinaBearerTokenAuthenticator(boolean challenge, ResourceMetadata resourceMetadata)
   {
      this.challenge = challenge;
      this.resourceMetadata = resourceMetadata;
   }

   public ResourceMetadata getResourceMetadata()
   {
      return resourceMetadata;
   }

   public SkeletonKeyTokenVerification getVerification()
   {
      return verification;
   }

   public boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null)
      {
         if (challenge)
         {
            challengeResponse(response, null, null);
            return false;
         }
         else
         {
            return false;
         }
      }

      String[] split = authHeader.trim().split("\\s+");
      if (split == null || split.length != 2) challengeResponse(response, null, null);
      if (!split[0].equalsIgnoreCase("Bearer")) challengeResponse(response, null, null);


      String tokenString = split[1];


      try
      {
         X509Certificate[] chain = request.getCertificateChain();
         verification = RSATokenVerifier.verify(chain, tokenString, resourceMetadata);
      }
      catch (VerificationException e)
      {
         log.error("Failed to verify token", e);
         challengeResponse(response, "invalid_token", e.getMessage());
      }
      return true;
   }


   protected void challengeResponse(HttpServletResponse response, String error, String description) throws LoginException
   {
      StringBuilder header = new StringBuilder("Bearer realm=\"");
      header.append(resourceMetadata.getRealm()).append("\"");
      if (error != null)
      {
         header.append(", error=\"").append(error).append("\"");
      }
      if (description != null)
      {
         header.append(", error_description=\"").append(description).append("\"");
      }
      response.setHeader("WWW-Authenticate", header.toString());
      try
      {
         response.sendError(401);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      throw new LoginException("Challenged");
   }
}
