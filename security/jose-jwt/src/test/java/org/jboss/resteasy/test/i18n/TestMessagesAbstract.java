package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.jboss.logging.Logger;
import org.junit.Assert;

import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.CompressionAlgorithm;
import org.jboss.resteasy.jose.jwe.EncryptionMethod;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 28, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 3);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/jose/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "000", "algorithmOfSharedSymmetricKey"), Messages.MESSAGES.algorithmOfSharedSymmetricKey());
      Assert.assertEquals(getExpected(BASE + "015", "cekKeyLengthMismatch", 3, 7), Messages.MESSAGES.cekKeyLengthMismatch(3, 7));
      Assert.assertEquals(getExpected(BASE + "025", "contentEncryptionKeyLength", 11, EncryptionMethod.A256GCM), Messages.MESSAGES.contentEncryptionKeyLength(11, EncryptionMethod.A256GCM));
      Assert.assertEquals(getExpected(BASE + "155", "unsupportedCompressionAlgorithm", CompressionAlgorithm.DEF), Messages.MESSAGES.unsupportedCompressionAlgorithm(CompressionAlgorithm.DEF));
      Assert.assertEquals(getExpected(BASE + "175", "unsupportedKeyLength"), Messages.MESSAGES.unsupportedKeyLength());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
