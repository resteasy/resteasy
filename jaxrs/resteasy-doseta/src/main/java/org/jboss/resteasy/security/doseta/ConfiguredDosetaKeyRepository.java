package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.spi.ResteasyConfiguration;

import javax.ws.rs.core.Context;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * For use when you want repository created via a context object, i.e., when defined within a WAR file.
 *
 * For WAR files, it will look in context parameters and servlet/filter init params for doseta variables.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConfiguredDosetaKeyRepository implements KeyRepository
{
   public static String RESTEASY_KEY_STORE_FILE_NAME = "resteasy.doseta.keystore.filename";
   public static String RESTEASY_KEY_STORE_CLASSPATH = "resteasy.doseta.keystore.classpath";
   public static String RESTEASY_KEY_STORE_PASSWORD = "resteasy.doseta.keystore.password";
   public static String RESTEASY_DOSETA_USE_DNS = "resteasy.doseta.use.dns";
   public static String RESTEASY_DOSETA_DNS_URI = "resteasy.doseta.dns.uri";
   public static String RESTEASY_DOSETA_DEFAULT_PRIVATE_DOMAIN = "resteasy.doseta.default.private.domain";
   public static String RESTEASY_DOSETA_CACHE_TIMEOUT = "resteasy.doseta.cache.timeout";
   public static String RESTEASY_DOSETA_PRINCIPAL_FOR_PRIVATE = "resteasy.doseta.principal.for.private";

   protected DosetaKeyRepository keyRepository = new DosetaKeyRepository();

   private static String getVariable(ResteasyConfiguration config, String name)
   {
      String variable = config.getParameter(name);
      if (variable != null) variable = variable.trim();
      return variable;
   }

   public ConfiguredDosetaKeyRepository(@Context ResteasyConfiguration config)
   {
      String password = getVariable(config,RESTEASY_KEY_STORE_PASSWORD);
      keyRepository.setKeyStorePassword(password);

      String keyStoreFileName = getVariable(config,RESTEASY_KEY_STORE_FILE_NAME);
      keyRepository.setKeyStoreFile(keyStoreFileName);

      String keyStorePath = getVariable(config, RESTEASY_KEY_STORE_CLASSPATH);
      keyRepository.setKeyStorePath(keyStorePath);


      String principal = getVariable(config, RESTEASY_DOSETA_PRINCIPAL_FOR_PRIVATE);
      if (principal != null)
      {
         keyRepository.setUserPrincipalAsPrivateSelector(Boolean.parseBoolean(principal));
      }

      String useDns = getVariable(config, RESTEASY_DOSETA_USE_DNS);
      if (useDns != null)
      {
         keyRepository.setUseDns(Boolean.parseBoolean(useDns));
      }

      String dnsUri = getVariable(config, RESTEASY_DOSETA_DNS_URI);
      if (dnsUri != null)
      {
         keyRepository.setDnsUri(dnsUri.trim());
      }

      String defaultDomain = getVariable(config, RESTEASY_DOSETA_DEFAULT_PRIVATE_DOMAIN);
      keyRepository.setDefaultPrivateDomain(defaultDomain);

      String timeout = getVariable(config, RESTEASY_DOSETA_CACHE_TIMEOUT);
      if (timeout != null)
      {
         keyRepository.setCacheTimeout(Long.parseLong(timeout.trim()));
      }

      keyRepository.start();



   }

   public PrivateKey findPrivateKey(DosetaSignature header)
   {
      return keyRepository.findPrivateKey(header);
   }

   public PublicKey findPublicKey(DosetaSignature header)
   {
      return keyRepository.findPublicKey(header);
   }

   public String getDefaultPrivateSelector()
   {
      return keyRepository.getDefaultPrivateSelector();
   }

   public String getDefaultPrivateDomain()
   {
      return keyRepository.getDefaultPrivateDomain();
   }
}
