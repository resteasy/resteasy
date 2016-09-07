package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.junit.Assert;

import org.jboss.resteasy.plugins.providers.html.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 24, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/providers/html/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "noDispatcherFound", "path"), Messages.MESSAGES.noDispatcherFound("path"));
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
