package org.jboss.resteasy.testi18n;

import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

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
   
   public void before(Locale locale) throws Exception
   {
         System.out.println("default locale: " + Locale.getDefault());
         Locale.setDefault(locale);
         System.out.println("Set default locale to: " + locale);
         InputStream is = getClass().getClassLoader().getResourceAsStream("org/jboss/resteasy/i18n/Messages.i18n_" + locale.toString() + ".properties");
         properties.load(is);
   }
   
   @Test
   public void testLocale() throws Exception
   {  
      before(getLocale());
      
      Assert.assertEquals(getExpected("001020", "badBase64Character", 17, 29), Messages.MESSAGES.badBase64Character(17, 29));
      Assert.assertEquals(getExpected("001040", "cacheControlMaxAgeHeader", "header"), Messages.MESSAGES.cacheControlMaxAgeHeader("header"));
      Assert.assertEquals(getExpected("001125", "clientResponseFailureMediaType", new MediaType("xx", "yy"), getClass()), Messages.MESSAGES.clientResponseFailureMediaType(new MediaType("xx", "yy"), getClass()));  
      Assert.assertEquals(getExpected("001130", "clientResponseFailureStatus", Status.GONE.getStatusCode(), Status.GONE), Messages.MESSAGES.clientResponseFailureStatus(Status.GONE.getStatusCode(), Status.GONE));      
      Assert.assertEquals(getExpected("001135", "constructorMappingInvalid"), Messages.MESSAGES.constructorMappingInvalid());
      Assert.assertEquals(getExpected("001180", "couldNotFindResourceForFullPath", new URI("http://localhost")), Messages.MESSAGES.couldNotFindResourceForFullPath(new URI("http://localhost")));
      Assert.assertEquals(getExpected("001345", "illegalToInjectMessageBody", getClass().getMethod("testLocale")), Messages.MESSAGES.illegalToInjectMessageBody(getClass().getMethod("testLocale")));
      Assert.assertEquals(getExpected("001485", "methodNotAnnotatedWithPath"), Messages.MESSAGES.methodNotAnnotatedWithPath());
      Assert.assertEquals(getExpected("001615", "qValueCannotBeGreaterThan1", "x/y;q=1.1"), Messages.MESSAGES.qValueCannotBeGreaterThan1("x/y;q=1.1"));
      Assert.assertEquals(getExpected("001630", "requestMediaTypeNotUrlencoded"), Messages.MESSAGES.requestMediaTypeNotUrlencoded());
      Assert.assertEquals(getExpected("001635", "requestWasCommitted"), Messages.MESSAGES.requestWasCommitted());
      Assert.assertEquals(getExpected("001835", "unableToUnmarshalResponse", "abc", new Exception("unable")), Messages.MESSAGES.unableToUnmarshalResponse("abc"));
      Assert.assertEquals(getExpected("001865", "unsupportedCollectionType", getClass()), Messages.MESSAGES.unsupportedCollectionType(getClass()));
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
