package org.jboss.resteasy.security.doseta;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class KeyStoreKeyRepository
{
   private KeyStore keyStore;
   private String password;


   public void init(InputStream is, String password)
   {
      if (password != null) password = password.trim();
      this.password = password;
      try
      {
         keyStore = KeyStore.getInstance("JKS");
         keyStore.load(is, password.toCharArray());
      }
      catch (KeyStoreException e)
      {
         throw new RuntimeException(e);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      catch (CertificateException e)
      {
         throw new RuntimeException(e);
      }
   }

   public KeyStoreKeyRepository(InputStream is, String password)
   {
      init(is, password);
   }

   public KeyStoreKeyRepository(String filename, String password) throws IOException
   {
      InputStream is = new FileInputStream(filename);
      init(is, password);
      is.close();
   }

   public PrivateKey getPrivateKey(Object identity)
   {
      try
      {
         Key key = keyStore.getKey(identity.toString(), password.toCharArray());
         return (PrivateKey) key;
      }
      catch (KeyStoreException e)
      {
         throw new RuntimeException(e);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      catch (UnrecoverableKeyException e)
      {
         throw new RuntimeException(e);
      }
   }

   public PublicKey getPublicKey(Object identity)
   {
      Certificate cert = null;
      try
      {
         cert = keyStore.getCertificate(identity.toString());
      }
      catch (KeyStoreException e)
      {
         throw new RuntimeException(e);
      }
      if (cert == null) return null;
      return cert.getPublicKey();
   }
}
