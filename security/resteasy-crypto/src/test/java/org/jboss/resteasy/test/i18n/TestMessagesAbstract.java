package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.junit.Assert;

import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 29, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 3);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/security/doseta/i18n/Messages.i18n_" + locale.toString() + ".properties";
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
   
   @Override
   protected String getExpected(String id, String message, Object... args)
   {
      String s = super.getExpected(id, message, args);
      String ss = pruneQuotes(s);
      System.out.println("actual expected: " + ss);
      return ss;
   }
   
   protected String pruneQuotes(String s)
   {
      StringBuffer sb = new StringBuffer();
      boolean sawQuote = false;
      for (int i = 0; i < s.length(); i++)
      {
         char c = s.charAt(i);
         if (sawQuote)
         {
            sawQuote = false;
            sb.append('\'');
            if (c != '\'')
            {
               sb.append(c);
            }
         }
         else if (c == '\'')
         {
            sawQuote = true;
         }
         else
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }
   
   abstract protected Locale getLocale();
}
