package org.jboss.resteasy.test.i18n;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.resteasy.jaxrs_api.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 22, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   protected static final String BASE3 = BASE.substring(0, 3);
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
      Assert.assertEquals(getExpected(BASE + "05", "classCastException", testURL, testURL), Messages.MESSAGES.classCastException(testURL, testURL));
      Assert.assertEquals(getExpected(BASE + "70", "providerCouldNotBeInstantiated", "class", testException), Messages.MESSAGES.providerCouldNotBeInstantiated("class", testException));
      Assert.assertEquals(getExpected(BASE + "80", "statusNotFromRequiredFamily", 7, Response.Status.Family.OTHER), Messages.MESSAGES.statusNotFromRequiredFamily(7, Response.Status.Family.OTHER));
      Assert.assertEquals(getExpected(BASE3 + "625", "valueIsNull"), Messages.MESSAGES.valueIsNull());      
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
