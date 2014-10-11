package org.jboss.resteasy.test.i18n;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.jaxrs_api.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 11, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   protected static URL testURL;
   protected static Exception testException = new Exception("test");
   
   static
   {
      try
      {
         testURL = new URL("http://localhost");
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
   }
   

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/jaxrs_api/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "argumentsMustNotBeNull"), Messages.MESSAGES.argumentsMustNotBeNull());
      Assert.assertEquals(getExpected(BASE + "05", "classCastException", testURL, "type"), Messages.MESSAGES.classCastException(testURL, "type"));
      Assert.assertEquals(getExpected(BASE + "45", "providerCouldNotBeInstantiated", "class", testException), Messages.MESSAGES.providerCouldNotBeInstantiated("class", testException));
      Assert.assertEquals(getExpected(BASE + "55", "valueIsNull"), Messages.MESSAGES.valueIsNull());      
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
