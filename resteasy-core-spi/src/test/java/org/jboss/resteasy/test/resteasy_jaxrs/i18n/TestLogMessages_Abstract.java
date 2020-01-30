package org.jboss.resteasy.test.resteasy_jaxrs.i18n;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
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
   private static final Logger LOG = LogManager.getLogger(TestLogMessages_Abstract.class);

   protected static Locale savedLocale;

   protected static final String BASE = "002";

   protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

   public void before(Level level, Locale locale, String filename) throws Exception
   {
      super.before(locale, filename);
      baos.reset();
      PrintWriter writer = new PrintWriter(baos, true);
      addAppender(writer, "testAppender", level);
   }

   public void after()
   {
      removeAppender("testAppender");
   }

   private void addAppender(final Writer writer, final String writerName, final Level level) {
       final LoggerContext context = LoggerContext.getContext(false);
       final Configuration config = context.getConfiguration();
       final PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("[%d{ABSOLUTE}] [%t] %5p (%F:%L) - %m%n").build();
       final Appender appender = WriterAppender.createAppender(layout, null, writer, writerName, false, true);
       appender.start();
       config.addAppender(appender);
       updateLoggers(appender, config, level);
   }

   private void removeAppender(final String writerName) {
      final LoggerContext context = LoggerContext.getContext(false);
      final Configuration config = context.getConfiguration();
      config.getRootLogger().removeAppender(writerName);
   }

   private void updateLoggers(final Appender appender, final Configuration config, final Level level) {
       final Filter filter = null;
       for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
           loggerConfig.addAppender(appender, level, filter);
       }
       config.getRootLogger().addAppender(appender, level, filter);
   }

   @Test
   public void testLocale() throws Exception
   {
      String filename = "org/jboss/resteasy/resteasy_jaxrs/i18n/LogMessages.i18n_" + getLocale().toString()
            + ".properties";
      if (!(before(getLocale(), filename)))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
      doTest(getLocale(), filename);
   }

   protected void doTest(Locale locale, String filename) throws Exception
   {
      doTestFatal(locale, filename);
      doTestError(locale, filename);
      doTestWarn(locale, filename);
      doTestInfo(locale, filename);
      doTestDebug(locale, filename);
      doTestTrace(locale, filename);
   }

   protected void doTestFatal(Locale locale, String filename) throws Exception
   {
      before(Level.FATAL, getLocale(), filename);
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      LogMessages.LOGGER.couldNotDeleteFile("path", new Exception("Sluggo says"));
      Assert.assertEquals("", baos.toString());
      after();
   }

   protected void doTestError(Locale locale, String filename) throws Exception
   {
      before(Level.ERROR, getLocale(), filename);

      // ERROR
      LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
      LOG.info("actual: " + baos.toString());
      String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
      Assert.assertTrue(baos.toString().contains(expected));
      Assert.assertTrue(baos.toString().contains("java.lang.Exception"));
      Assert.assertTrue(baos.toString().contains("oh no mr bill"));
      baos.reset();

      // WARN
      LogMessages.LOGGER.couldNotDeleteFile("path3", new Exception("Sluggo says"));
      Assert.assertEquals("", baos.toString());
      after();
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
      after();
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
      after();
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
      after();
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
      after();
   }

   @Override
   protected int getExpectedNumberOfMethods()
   {
      LOG.info("expected number of methods: " + LogMessages.class.getDeclaredMethods().length);
      return LogMessages.class.getDeclaredMethods().length;
   }

   protected abstract Locale getLocale();
}
