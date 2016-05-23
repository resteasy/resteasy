package org.jboss.resteasy.jose.jwe;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jboss.resteasy.jose.i18n.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public enum EncryptionMethod
{
   A128CBC_HS256(256),
   A256CBC_HS512(512),
   A128GCM(128),
   A256GCM(256)
   ;
   private int cekBitLength;

   private EncryptionMethod(int cekBitLength)
   {
      this.cekBitLength = cekBitLength;
   }

   public int getCekBitLength()
   {
      return cekBitLength;
   }

   public MessageDigest createSecretDigester()
   {
      try
      {
         switch (cekBitLength)
         {
            case 128:
               return MessageDigest.getInstance("MD5");
            case 256:
               return MessageDigest.getInstance("SHA-256");
            case 512:
               return MessageDigest.getInstance("SHA-512");
         }
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(e);
      }
      throw new IllegalStateException(Messages.MESSAGES.unknownLength());
   }
}
