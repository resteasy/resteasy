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
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.Principal;
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

      String idpTruststore = (String) options.get("idp-truststore");
      String idpTruststorePassword = (String) options.get("idp-truststore-password");
      String idpAliases = (String) options.get("idp-aliases");
      if (idpTruststore == null)
      {
         throw new IllegalArgumentException("Must set idp-truststore in security domain config");
      }
      if (idpTruststorePassword == null)
      {
         throw new IllegalArgumentException("Must set idp-truststore-password in security domain config");
      }
      if (idpAliases == null)
      {
         throw new IllegalArgumentException("Must set idp-aliases in security domain config");
      }


      KeyStore idp = null;
      try
      {
         idp = loadKeyStore(idpTruststore, idpTruststorePassword);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      String[] aliases = idpAliases.split(",");
      ArrayList<X509Certificate> idpCerts = new ArrayList<X509Certificate>();
      for (String alias : aliases)
      {
         X509Certificate cert = null;
         try
         {
            cert = (X509Certificate)idp.getCertificate(alias.trim());
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         if (cert == null) throw new RuntimeException("Could not find IDP alias: " + alias);
         idpCerts.add(cert);
      }
      serviceMetadata = new ServiceMetadata();
      serviceMetadata.setRealm(domain);
      serviceMetadata.setName(name);
      serviceMetadata.setIdentityProviderCertificates(idpCerts.toArray(new X509Certificate[idpCerts.size()]));



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

   protected String getQueryParamToken(String queryString)
   {
      if (queryString == null) return null;
      String[] params = queryString.split("&");

      for (String param : params)
      {
         if (param.indexOf('=') >= 0)
         {
            String[] nv = param.split("=");
            try
            {
               String name = URLDecoder.decode(nv[0], "UTF-8");
               String val = nv.length > 1 ? nv[1] : "";
               if (name.equals("skeleton_token")) return val;
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
      return null;

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
