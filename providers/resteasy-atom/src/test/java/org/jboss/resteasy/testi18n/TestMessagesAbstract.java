package org.jboss.resteasy.testi18n;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.i18n.Messages;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 27, 2014
 */
abstract public class TestMessagesAbstract
{
   static protected Locale savedLocale;
   protected Properties properties = new Properties();
   
   protected int testField;

   @BeforeClass
   static public void beforeClass()
   {
      savedLocale = Locale.getDefault();  
   }
   
   @AfterClass
   static public void afterClass()
   {
      Locale.setDefault(savedLocale);
      System.out.println("Reset default locale to: " + savedLocale);
   }
   
   public boolean before(Locale locale) throws Exception
   {
         System.out.println("default locale: " + Locale.getDefault());
         Locale.setDefault(locale);
         System.out.println("Set default locale to: " + locale);
         System.out.println("getting org/jboss/resteasy/i18n/Messages.i18n_" + locale.toString() + ".properties");
         InputStream is = getClass().getClassLoader().getResourceAsStream("org/jboss/resteasy/i18n/Messages.i18n_" + locale.toString() + ".properties");
         if (is == null)
         {
            return false;
         }
         properties.load(is);
         return (Messages.class.getDeclaredMethods().length == properties.size());
   }
   
   @Test
   public void testLocale() throws Exception
   {  
      if (!before(getLocale()))
      {
         System.out.println(getClass() + ": org/jboss/resteasy/i18n/Messages.i18n_" + getLocale() + ".properties not found.");
         return;
      }
      
      Assert.assertEquals(getExpected("002000", "consructorMustBeCalled"), Messages.MESSAGES.consructorMustBeCalled());
      Assert.assertEquals(getExpected("002005", "unableToFindJAXBContext", "xx/yy"), Messages.MESSAGES.unableToFindJAXBContext(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected("002010", "unableToMarshal", "xx/yy"), Messages.MESSAGES.unableToMarshal(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected("002015", "unableToUnmarshal", "xx/yy"), Messages.MESSAGES.unableToUnmarshal(new MediaType("xx", "yy")));
   }
   
   protected String getExpected(String id, String message, Object... args)
   {
      String expected = "RESTEASY" + id + ": " + String.format(replacePositionalSpecifiers(String.class.cast(properties.get(message))), args);
      System.out.println("expected: " + expected);
      return expected;    
   }
   
   protected String replacePositionalSpecifiers(String s)
   {
//      System.out.println("before: " + s);
      int pos0 = s.indexOf("{0}");
      if (pos0 > -1)
      {
         s = s.substring(0, pos0) + "%1$s" + (pos0 + 3 >= s.length() ? "" : s.substring(pos0 + 3));
      }
      int pos1 = s.indexOf("{1}");
      if (pos1 > -1)
      {
         s = s.substring(0, pos1) + "%2$s" + (pos1 + 3 >= s.length() ? "" : s.substring(pos1 + 3));
      }
      int pos2 = s.indexOf("{2}");
      if (pos2 > -1)
      {
         s = s.substring(0, pos2) + "%3$s" + (pos2 + 3 >= s.length() ? "" : s.substring(pos2 + 3));
      }
//      System.out.println("after: " + s);
      return s;
   }
   
   abstract protected Locale getLocale();
}
