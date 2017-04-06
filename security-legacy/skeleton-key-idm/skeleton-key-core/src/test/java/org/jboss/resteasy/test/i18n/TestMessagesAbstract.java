package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.junit.Assert;

import org.jboss.resteasy.skeleton.key.i18n.Messages;
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
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/skeleton/key/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "codeParameterWasNull"), Messages.MESSAGES.codeParameterWasNull());
      Assert.assertEquals(getExpected(BASE + "20", "mustSetRealmInConfig"), Messages.MESSAGES.mustSetRealmInConfig());
      Assert.assertEquals(getExpected(BASE + "30", "oAuthError", "error"), Messages.MESSAGES.oAuthError("error"));
      Assert.assertEquals(getExpected(BASE + "55", "tokenAudienceDoesntMatchDomain"), Messages.MESSAGES.tokenAudienceDoesntMatchDomain());
      Assert.assertEquals(getExpected(BASE + "75", "unknownErrorGettingAccessToken"), Messages.MESSAGES.unknownErrorGettingAccessToken());      
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
