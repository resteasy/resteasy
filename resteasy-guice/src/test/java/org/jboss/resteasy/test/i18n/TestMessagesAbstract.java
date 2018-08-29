package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.jboss.logging.Logger;
import org.junit.Assert;

import org.jboss.resteasy.plugins.guice.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 27, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/guice/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
     
      Assert.assertEquals(getExpected(BASE + "00", "cannotExecute", "abc", "def", "ghi"), Messages.MESSAGES.cannotExecute("abc", "def", "ghi"));
      Assert.assertEquals(getExpected(BASE + "15", "problemRunningAnnotationMethod", "annotation"), Messages.MESSAGES.problemRunningAnnotationMethod("annotation"));
      Assert.assertEquals(getExpected(BASE + "25", "registeringProviderInstance", "provider"), Messages.MESSAGES.registeringProviderInstance("provider"));
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
      LOG.info("actual expected: " + ss);
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
