package org.jboss.resteasy.test.i18n;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 26, 2014
 */
public abstract class TestLogMessages_Abstract
{
   static protected Locale savedLocale;
   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

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
   
   public void before(Level level, Locale locale) throws Exception
   {
      if (locale != null)
      {
         System.out.println("default locale: " + Locale.getDefault());
         Locale.setDefault(locale);
         System.out.println("Set default locale to: " + locale);
      }
      System.out.println("locale: " + Locale.getDefault());
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
   @Ignore // No log messages in this modules
   public void testLocale() throws Exception
   {
      doTest(getLocale(), failedExecuting(), couldNotDeleteFile(), deploying(), creatingContextObject());
   }
   
   protected void doTest(Locale locale, String failedExecuting, String couldNotDelete, String deployingClass, String creatingContextObject) throws Exception
   {
      doTestFatal(locale);
      doTestError(locale, failedExecuting);
      doTestWarn (locale, failedExecuting, couldNotDelete);
      doTestInfo (locale, failedExecuting, couldNotDelete, deployingClass);
      doTestDebug(locale, failedExecuting, couldNotDelete, deployingClass, creatingContextObject);
      doTestTrace(locale, failedExecuting, couldNotDelete, deployingClass, creatingContextObject);
   }

   
   protected void doTestFatal(Locale locale) throws Exception
   {  
      before(Level.FATAL, locale);
      Assert.assertEquals("", baos.toString());
   }

   protected void doTestError(Locale locale, String failedExecuting) throws Exception
   {  
      before(Level.ERROR, locale);

      // ERROR

      // WARN
   }

   protected void doTestWarn(Locale locale, String failedExecuting, String couldNotDelete) throws Exception
   {  
      before(Level.WARN, locale);

      // ERROR

      // WARN

      // INFO
   }

   protected void doTestInfo(Locale locale, String failedExecuting, String couldNotDelete, String deployingClass) throws Exception
   {  
      before(Level.INFO, locale);

      // ERROR

      // WARN

      // INFO

      // DEBUG
   }

   protected void doTestDebug(Locale locale, String failedExecuting, String couldNotDelete, String deployingClass, String creatingContextObject) throws Exception
   {  
      before(Level.DEBUG, locale);

      // ERROR

      // WARN

      // INFO

      // DEBUG
   }

   protected void doTestTrace(Locale locale, String failedExecuting, String couldNotDelete, String deployingClass, String creatingContextObject) throws Exception
   {  
      before(Level.TRACE, locale);

      // ERROR

      // WARN

      // INFO

      // DEBUG
   }
   
   abstract protected Locale getLocale();
   abstract protected String failedExecuting();
   abstract protected String couldNotDeleteFile();
   abstract protected String deploying();
   abstract protected String creatingContextObject();
}
