package org.jboss.resteasy.test.resteasy_jaxrs.i18n;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 20, 2015
 */
public abstract class TestLogMessages_Abstract extends TestMessagesParent
{
   static protected Locale savedLocale;
   protected static final String BASE = "002";
   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
   
   public void before(Level level, Locale locale, String filename) throws Exception
   {
      super.before(locale, filename);
      String pattern = "[%d{ABSOLUTE}] [%t] %5p (%F:%L) - %m%n";
      PatternLayout layout = new PatternLayout(pattern);
      ConsoleAppender consoleAppender = new ConsoleAppender(layout);
      baos.reset();
      PrintWriter writer = new PrintWriter(baos, true);
      consoleAppender.setWriter(writer);
      Logger.getLogger("org.jboss.resteasy").addAppender(consoleAppender);
      Logger.getLogger("org.jboss.resteasy").setLevel(level);
      System.out.println("org.jboss.resteasy Level: " + Logger.getLogger("org.jboss.resteasy").getEffectiveLevel());
   }

   @Test
   public void testLocale() throws Exception
   {
      String filename = "org/jboss/resteasy/resteasy_jaxrs/i18n/LogMessages.i18n_" + getLocale().toString() + ".properties";
      if (!(before(getLocale(), filename)))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      doTest(getLocale(), filename);
   }
   
   protected void doTest(Locale locale, String filename) throws Exception
   {
      doTestFatal(locale, filename);
      doTestError(locale, filename);
      doTestWarn (locale, filename);
      doTestInfo (locale, filename);
      doTestDebug(locale, filename);
      doTestTrace(locale, filename);
   }

   
   protected void doTestFatal(Locale locale, String filename) throws Exception
   {  
      before(Level.FATAL, getLocale(), filename);
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      LogMessages.LOGGER.couldNotDeleteFile("path", new Exception("Sluggo says"));
      Assert.assertEquals("", baos.toString());
   }

   protected void doTestError(Locale locale, String filename) throws Exception
   {  
      before(Level.ERROR, getLocale(), filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      System.out.println("actual: " + baos.toString());
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("path3", new Exception("Sluggo says"));
      Assert.assertEquals("", baos.toString());
   }

   protected void doTestWarn(Locale locale, String filename) throws Exception
   {  
      before(Level.WARN, locale, filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
      expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("Sluggo says"));
      baos.reset();

      // INFO
      LogMessages.LOGGER.deployingApplication("class", getClass());
      Assert.assertEquals("", baos.toString());
   }

   protected void doTestInfo(Locale locale, String filename) throws Exception
   {  
      before(Level.INFO, locale, filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
      expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("Sluggo says"));
      baos.reset();

      // INFO
      LogMessages.LOGGER.deployingApplication("className", getClass());
      expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
      Assert.assertTrue(baos.toString().contains(expected));
      baos.reset();

      // DEBUG
      LogMessages.LOGGER.creatingContextObject("key", "value");
      Assert.assertEquals("", baos.toString());
   }

   protected void doTestDebug(Locale locale, String filename) throws Exception
   {  
      before(Level.DEBUG, locale, filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
      expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("Sluggo says"));
      baos.reset();

      // INFO
      LogMessages.LOGGER.deployingApplication("className", getClass());
      expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
      Assert.assertTrue(baos.toString().contains(expected));
      baos.reset();

      // DEBUG
      LogMessages.LOGGER.creatingContextObject("key", "value");
      expected = getExpected(BASE + "300", "creatingContextObject", "key", "value");
      Assert.assertTrue(baos.toString().contains(expected));
   }

   protected void doTestTrace(Locale locale, String filename) throws Exception
   {  
      before(Level.TRACE, locale, filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
      expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("Sluggo says"));
      baos.reset();

      // INFO
      LogMessages.LOGGER.deployingApplication("className", getClass());
      expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
      Assert.assertTrue(baos.toString().contains(expected));
      baos.reset();

      // DEBUG
      LogMessages.LOGGER.creatingContextObject("key", "value");
      expected = getExpected(BASE + "300", "creatingContextObject", "key", "value");
      Assert.assertTrue(baos.toString().contains(expected));
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      System.out.println("expected number of methods: " +LogMessages.class.getDeclaredMethods().length);
      return LogMessages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
