package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.authenticator.AuthenticatorBase;
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
import org.jboss.resteasy.skeleton.key.as7.config.RemoteSkeletonKeyConfig;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractRemoteOAuthAuthenticatorValve extends AuthenticatorBase
{
   private static final Logger log = Logger.getLogger(AbstractRemoteOAuthAuthenticatorValve.class);
   protected RemoteSkeletonKeyConfig remoteSkeletonKeyConfig;
   protected ResourceMetadata resourceMetadata;

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
   public void start() throws LifecycleException
   {
      super.start();
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      InputStream is = context.getServletContext().getResourceAsStream("/WEB-INF/resteasy-oauth.json");
      remoteSkeletonKeyConfig = null;
      try
      {
         remoteSkeletonKeyConfig = mapper.readValue(is, RemoteSkeletonKeyConfig.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      String name = remoteSkeletonKeyConfig.getResource();
      String realm = remoteSkeletonKeyConfig.getRealm();
      if (realm == null) throw new RuntimeException("Must set 'realm' in config");

      String realmKeyPem = remoteSkeletonKeyConfig.getRealmKey();
      if (realmKeyPem == null)
      {
         throw new IllegalArgumentException("You must set the realm-public-key");
      }

      PublicKey realmKey = null;
      try
      {
         realmKey = PemUtils.decodePublicKey(realmKeyPem);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      resourceMetadata = new ResourceMetadata();
      resourceMetadata.setRealm(realm);
      resourceMetadata.setResourceName(name);
      resourceMetadata.setRealmKey(realmKey);


      String truststore = remoteSkeletonKeyConfig.getTruststore();
      if (truststore != null)
      {
         truststore = EnvUtil.replace(truststore);
         String truststorePassword = remoteSkeletonKeyConfig.getTruststorePassword();
         KeyStore trust = null;
         try
         {
            trust = loadKeyStore(truststore, truststorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to load truststore", e);
         }
         resourceMetadata.setTruststore(trust);
      }
      String clientKeystore = remoteSkeletonKeyConfig.getClientKeystore();
      String clientKeyPassword = null;
      if (clientKeystore != null)
      {
         clientKeystore = EnvUtil.replace(clientKeystore);
         String clientKeystorePassword = remoteSkeletonKeyConfig.getClientKeystorePassword();
         KeyStore serverKS = null;
         try
         {
            serverKS = loadKeyStore(clientKeystore, clientKeystorePassword);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to load keystore", e);
         }
         resourceMetadata.setClientKeystore(serverKS);
         clientKeyPassword = remoteSkeletonKeyConfig.getClientKeyPassword();
         resourceMetadata.setClientKeyPassword(clientKeyPassword);
      }

   }
}
