package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.security.doseta.i18n.LogMessages;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.ParameterParser;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.ws.rs.core.SecurityContext;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DosetaKeyRepository implements KeyRepository
{

   protected class CacheEntry<T>
   {
      public long time = System.currentTimeMillis();
      public T key;

      protected CacheEntry(T key)
      {
         this.key = key;
      }

      public boolean isStale()
      {
         return time + cacheTimeout >= System.currentTimeMillis();
      }
   }

   protected ConcurrentHashMap<String, CacheEntry<PrivateKey>> privateCache = new ConcurrentHashMap<String, CacheEntry<PrivateKey>>();
   protected ConcurrentHashMap<String, CacheEntry<PublicKey>> publicCache = new ConcurrentHashMap<String, CacheEntry<PublicKey>>();
   protected KeyStoreKeyRepository keyStore;
   protected String defaultPrivateDomain;
   protected boolean useDns = false;
   protected boolean userPrincipalAsPrivateSelector = false;
   protected String dnsUri;
   protected long cacheTimeout = 3600000l; // 1 hour
   protected String keyStorePath;
   protected String keyStoreFile;
   protected String keyStorePassword;

   public void start()
   {
      if (keyStore == null)
      {
         if (keyStoreFile != null)
         {
            try
            {
               keyStore = new KeyStoreKeyRepository(keyStoreFile, keyStorePassword);
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
         else
         {
            if (keyStorePath != null)
            {
               InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath.trim());
               if (is == null) throw new RuntimeException(Messages.MESSAGES.unableToFindKeyStore(keyStorePath));
               keyStore = new KeyStoreKeyRepository(is, keyStorePassword);
               try
               {
                  is.close();
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      }
   }

   public String getDefaultPrivateSelector()
   {
      if (userPrincipalAsPrivateSelector)
      {
         SecurityContext securityContext = ResteasyProviderFactory.getContextData(SecurityContext.class);
         if (securityContext != null)
         {
            return securityContext.getUserPrincipal().getName();
         }
      }
      return null;
   }

   public String getKeyStorePath()
   {
      return keyStorePath;
   }

   public void setKeyStorePath(String keyStorePath)
   {
      this.keyStorePath = keyStorePath;
   }

   public String getKeyStoreFile()
   {
      return keyStoreFile;
   }

   public void setKeyStoreFile(String keyStoreFile)
   {
      this.keyStoreFile = keyStoreFile;
   }

   public String getKeyStorePassword()
   {
      return keyStorePassword;
   }

   public void setKeyStorePassword(String keyStorePassword)
   {
      this.keyStorePassword = keyStorePassword;
   }

   public KeyStoreKeyRepository getKeyStore()
   {
      return keyStore;
   }

   public void setKeyStore(KeyStoreKeyRepository keyStore)
   {
      this.keyStore = keyStore;
   }

   public String getDefaultPrivateDomain()
   {
      return defaultPrivateDomain;
   }

   public void setDefaultPrivateDomain(String defaultPrivateDomain)
   {
      this.defaultPrivateDomain = defaultPrivateDomain;
   }

   public boolean isUseDns()
   {
      return useDns;
   }

   public void setUseDns(boolean useDns)
   {
      this.useDns = useDns;
   }

   public boolean isUserPrincipalAsPrivateSelector()
   {
      return userPrincipalAsPrivateSelector;
   }

   public void setUserPrincipalAsPrivateSelector(boolean userPrincipalAsPrivateSelector)
   {
      this.userPrincipalAsPrivateSelector = userPrincipalAsPrivateSelector;
   }

   public String getDnsUri()
   {
      return dnsUri;
   }

   public void setDnsUri(String dnsUri)
   {
      this.dnsUri = dnsUri;
   }

   public long getCacheTimeout()
   {
      return cacheTimeout;
   }

   public void setCacheTimeout(long cacheTimeout)
   {
      this.cacheTimeout = cacheTimeout;
   }

   protected void addPrivate(String alias, PrivateKey key)
   {
      privateCache.put(alias, new CacheEntry<PrivateKey>(key));
   }

   protected void addPublic(String alias, PublicKey key)
   {
      publicCache.put(alias, new CacheEntry<PublicKey>(key));
   }

   protected PrivateKey getPrivateCache(String alias)
   {
      CacheEntry<PrivateKey> entry = privateCache.get(alias);
      if (entry == null) return null;
      if (entry.isStale())
      {
         privateCache.remove(alias, entry);
         return null;
      }
      return entry.key;
   }

   protected PublicKey getPublicCache(String alias)
   {
      CacheEntry<PublicKey> entry = publicCache.get(alias);
      if (entry == null) return null;
      if (entry.isStale())
      {
         publicCache.remove(alias, entry);
         return null;
      }
      return entry.key;
   }

   public String getAlias(DKIMSignature header)
   {
      StringBuffer buf = new StringBuffer();
      String selector = header.getSelector();
      if (selector != null) buf.append(selector.trim()).append(".");
      buf.append("_domainKey.");
      String domain = header.getDomain();
      if (domain == null)
      {
         throw new RuntimeException(Messages.MESSAGES.domainAttributeIsRequired());
      }
      buf.append(domain);
      return buf.toString();
   }

   public PrivateKey findPrivateKey(DKIMSignature header)
   {
      String alias = getAlias(header);
      if (alias == null) return null;
      PrivateKey key = getPrivateCache(alias);
      if (key != null) return key;

      if (keyStore != null)
      {
         key = keyStore.getPrivateKey(alias);
         if (key != null) addPrivate(alias, key);
      }

      return key;
   }

   public PublicKey findPublicKey(DKIMSignature header)
   {
      String alias = getAlias(header); // never use principal to find a public key
      if (alias == null) return null;
      PublicKey key = getPublicCache(alias);
      if (key != null) return key;

      if (keyStore != null)
      {
         key = keyStore.getPublicKey(alias);
         if (key != null) addPublic(alias, key);
      }

      if (useDns)
      {
         key = findFromDns(alias);
         addPublic(alias, key);
      }


      return key;
   }

   protected PublicKey findFromDns(String alias)
   {
      if (LogMessages.LOGGER.isDebugEnabled()) LogMessages.LOGGER.debug(Messages.MESSAGES.checkDNS(alias));
      PublicKey key;
      try
      {
         Hashtable<String, String> env = new Hashtable<String, String>();
         env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         if (dnsUri != null)
         {
            env.put("java.naming.provider.url", dnsUri);
         }
         DirContext dnsContext = new InitialDirContext(env);

         Attributes attrs1 = dnsContext.getAttributes(alias, new String[]{"TXT"});
         javax.naming.directory.Attribute txtrecord = attrs1.get("txt");
         String record = txtrecord.get().toString();
         dnsContext.close();
         if (LogMessages.LOGGER.isDebugEnabled()) LogMessages.LOGGER.debug(Messages.MESSAGES.dnsRecordFound(record));
         ParameterParser parser = new ParameterParser();
         parser.setLowerCaseNames(true);
         Map<String, String> keyEntry = parser.parse(record, ';');
         String type = keyEntry.get("k");
         if (type != null && !type.toLowerCase().equals("rsa"))
            throw new RuntimeException(Messages.MESSAGES.unsupportedKeyType(type));
         String pem = keyEntry.get("p");
         if (pem == null)
         {
            throw new RuntimeException(Messages.MESSAGES.noPEntry());
         }
         if (LogMessages.LOGGER.isDebugEnabled()) LogMessages.LOGGER.debug(Messages.MESSAGES.pem(pem));
         byte[] der = Base64.decode(pem);


         X509EncodedKeySpec spec =
                 new X509EncodedKeySpec(der);
         KeyFactory kf = KeyFactory.getInstance("RSA");
         key = kf.generatePublic(spec);

      }
      catch (Exception e)
      {
         throw new RuntimeException(Messages.MESSAGES.failedToFindPublicKey(alias), e);
      }
      return key;
   }


}
