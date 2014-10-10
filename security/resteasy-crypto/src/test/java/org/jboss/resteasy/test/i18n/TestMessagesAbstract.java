package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.crypto.i18n.Messages;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 9, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 3);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/crypto/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "500", "bodyHashesDoNotMatch"), Messages.MESSAGES.bodyHashesDoNotMatch());
      Assert.assertEquals(getExpected(BASE + "525", "couldNotFindPublicKey", new DKIMSignature()), Messages.MESSAGES.couldNotFindPublicKey(new DKIMSignature()));
      Assert.assertEquals(getExpected(BASE + "540", "expectedValue", "x", "y", "z"), Messages.MESSAGES.expectedValue("x", "y", "z"));
      Assert.assertEquals(getExpected(BASE + "650", "unsupportedKeyType", "xyz"), Messages.MESSAGES.unsupportedKeyType("xyz"));
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
