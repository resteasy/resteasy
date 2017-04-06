package org.jboss.resteasy.skeleton.key.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.skeleton.key.EnvUtil;
import org.jboss.resteasy.skeleton.key.PemUtils;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.i18n.Messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ManagedResourceConfigLoader
{
   protected ManagedResourceConfig remoteSkeletonKeyConfig;
   protected ResourceMetadata resourceMetadata;

   public static KeyStore loadKeyStore(String filename, String password) throws Exception
   {
      KeyStore trustStore = KeyStore.getInstance(KeyStore
              .getDefaultType());
      File truststoreFile = new File(filename);
      FileInputStream trustStream = new FileInputStream(truststoreFile);
      trustStore.load(trustStream, password.toCharArray());
      trustStream.close();
      return trustStore;
   }

   protected void init(InputStream is)
   {ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      remoteSkeletonKeyConfig = null;
      try
      {
         remoteSkeletonKeyConfig = mapper.readValue(is, ManagedResourceConfig.class);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      String name = remoteSkeletonKeyConfig.getResource();
      String realm = remoteSkeletonKeyConfig.getRealm();
      if (realm == null) throw new RuntimeException(Messages.MESSAGES.mustSetRealmInConfig());

      String realmKeyPem = remoteSkeletonKeyConfig.getRealmKey();
      if (realmKeyPem == null)
      {
         throw new IllegalArgumentException(Messages.MESSAGES.mustSetRealmPublicKey());
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
            throw new RuntimeException(Messages.MESSAGES.failedToLoadTruststore(), e);
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
            throw new RuntimeException(Messages.MESSAGES.failedToLoadKeystore(), e);
         }
         resourceMetadata.setClientKeystore(serverKS);
         clientKeyPassword = remoteSkeletonKeyConfig.getClientKeyPassword();
         resourceMetadata.setClientKeyPassword(clientKeyPassword);
      }
   }

   public ManagedResourceConfig getRemoteSkeletonKeyConfig()
   {
      return remoteSkeletonKeyConfig;
   }

   public ResourceMetadata getResourceMetadata()
   {
      return resourceMetadata;
   }
}
