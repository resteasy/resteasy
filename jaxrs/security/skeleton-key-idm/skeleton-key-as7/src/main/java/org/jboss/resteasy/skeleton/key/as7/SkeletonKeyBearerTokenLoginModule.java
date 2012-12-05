package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ServiceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PublicKey;
import java.security.acl.Group;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyBearerTokenLoginModule extends JBossWebAuthLoginModule
{
   /*

            <connector name="https" protocol="HTTP/1.1" scheme="https" socket-binding="https" secure="true">
                <ssl name="ssl" key-alias="server" password="password" certificate-key-file="c:/Users/William/jboss/application.jks" verify-client="want" ca-certificate-file="c:/Users/William/jboss/truststore.ts" ca-certificate-password="password"/>
            </connector>

                                                <login-module code="org.jboss.resteasy.skeleton.key.as7.SkeletonKeyBearerTokenLoginModule" flag="required" module="org.jboss.resteasy.skeleton-key">
                            <module-option name="idp-realm" value="MyRealm"/>
                            <module-option name="resource-name" value="MyService"/>
                            <module-option name="idp-truststore" value="C:/Users/William/jboss/idpTrust.ts"/>
                            <module-option name="idp-truststore-password" value="password"/>
                            <module-option name="idp-aliases" value="idp"/>
                        </login-module>


    */

   static ConcurrentHashMap<String, ServiceMetadata> serviceMetadataCache = new ConcurrentHashMap<String, ServiceMetadata>();

   protected ServiceMetadata serviceMetadata;
   protected SkeletonKeyTokenVerification verification;

   private static KeyStore loadKeyStore(String filename, String password) throws Exception
   {
      KeyStore trustStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File truststoreFile = new File(filename);
      FileInputStream trustStream = new FileInputStream(truststoreFile);
      trustStore.load(trustStream, password.toCharArray());
      trustStream.close();
      return trustStore;
   }


   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);
      String name = (String) options.get("resource-name");
      if (name == null) throw new RuntimeException("Must set resource-name in security domain config");
      String domain = (String) options.get("realm");
      if (domain == null) throw new RuntimeException(("Must set realm in security domain config"));

      String cacheKey = domain + ":" + name;
      serviceMetadata = serviceMetadataCache.get(cacheKey);
      if (serviceMetadata != null) return;

      String realmTruststore = (String) options.get("realm-truststore");
      String realmTruststorePassword = (String) options.get("realm-truststore-password");
      String realmAlias = (String) options.get("realm-key-alias");
      if (realmTruststore == null)
      {
         throw new IllegalArgumentException("Must set realm-truststore in security domain config");
      }
      if (realmTruststorePassword == null)
      {
         throw new IllegalArgumentException("Must set realm-truststore-password in security domain config");
      }
      if (realmAlias == null)
      {
         throw new IllegalArgumentException("Must set realm-key-alias in security domain config");
      }

      PublicKey realmKey = null;
      try
      {
         KeyStore realmKeystore = loadKeyStore(realmTruststore, realmTruststorePassword);
         X509Certificate cert = (X509Certificate)realmKeystore.getCertificate(realmAlias);
         if (cert == null) throw new RuntimeException("Could not find realm key: " + realmAlias);
         realmKey = cert.getPublicKey();

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      serviceMetadata = new ServiceMetadata();
      serviceMetadata.setRealm(domain);
      serviceMetadata.setName(name);
      serviceMetadata.setRealmKey(realmKey);



      String truststore = (String) options.get("truststore");
      if (truststore != null)
      {
         String truststorePassword = (String) options.get("truststore-password");
         KeyStore trust = null;
         try
         {
            trust = loadKeyStore(truststore, truststorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         serviceMetadata.setTruststore(trust);
      }
      String serverKeystore = (String) options.get("server-keystore");
      if (serverKeystore != null)
      {
         String serverKeystorePassword = (String) options.get("server-keystore-password");
         KeyStore serverKS = null;
         try
         {
            serverKS = loadKeyStore(serverKeystore, serverKeystorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         serviceMetadata.setKeystore(serverKS);
      }
      serviceMetadataCache.putIfAbsent(cacheKey, serviceMetadata);
   }

   protected void challengeResponse(HttpServletResponse response, String error, String description) throws LoginException
   {
      StringBuilder header = new StringBuilder("Bearer realm=\"");
      header.append(serviceMetadata.getRealm()).append("\"");
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

   @Override
   protected boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      X509Certificate[] chain = request.getCertificateChain();
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null)
      {
         challengeResponse(response, null, null);
      }

      String[] split = authHeader.trim().split("\\s+");
      if (split == null || split.length != 2) challengeResponse(response, null, null);
      if (!split[0].equalsIgnoreCase("Bearer")) challengeResponse(response, null, null);


      String tokenString = split[1];


      try
      {
         verification = RSATokenVerifier.verify(chain, tokenString, serviceMetadata);
         System.out.println(verification.getPrincipal().getName());
         System.out.println(verification.getRoles());
      }
      catch (VerificationException e)
      {
         log.error("Failed to verify token", e);
         challengeResponse(response, "invalid_token", e.getMessage());
      }
      this.loginOk = true;
      return true;
   }

   @Override
   protected Principal getIdentity()
   {
      return verification.getPrincipal();
   }

   @Override
   protected Group[] getRoleSets() throws LoginException
   {
      SimpleGroup roles = new SimpleGroup("Roles");
      Group[] roleSets = {roles};
      for (String role : verification.getRoles())
      {
         roles.addMember(new SimplePrincipal(role));
      }
      return roleSets;
   }

}
