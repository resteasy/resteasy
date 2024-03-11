package org.jboss.resteasy.test.resteasy_jaxrs.i18n;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.logging.Logger;

import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogContext;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.WriterHandler;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 20, 2015
 */
public abstract class TestLogMessages_Abstract extends TestMessagesParent {
    private static final LogContext TEST_CONTEXT = LogContext.create();
    private static final Logger LOG = TEST_CONTEXT.getLogger(TestLogMessages_Abstract.class.getName());

    static {
        LogContext.setLogContextSelector(() -> TEST_CONTEXT);
    }

    protected static Locale savedLocale;

    protected static final String BASE = "002";

    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public void before(Level level, Locale locale, String filename) throws Exception {
        super.before(locale, filename);
        baos.reset();
        PrintWriter writer = new PrintWriter(baos, true);
        addAppender(writer, level);
    }

    public void after() {
        TEST_CONTEXT.getLogger("").clearHandlers();
    }

    private void addAppender(final Writer writer, final Level level) {
        final PatternFormatter formatter = new PatternFormatter("%d{HH:mm:ss,SSS} [%t] %5p (%F:%L) - %s%e%n");
        final WriterHandler handler = new WriterHandler();
        handler.setFormatter(formatter);
        handler.setWriter(writer);
        final Logger root = TEST_CONTEXT.getLogger("");
        root.addHandler(handler);
        root.setLevel(level);
    }

    @Test
    public void testLocale() throws Exception {
        String filename = "org/jboss/resteasy/resteasy_jaxrs/i18n/LogMessages.i18n_" + getLocale().toString()
                + ".properties";
        if (!(before(getLocale(), filename))) {
            LOG.info(getClass() + ": " + filename + " not found.");
            return;
        }
        doTest(getLocale(), filename);
    }

    protected void doTest(Locale locale, String filename) throws Exception {
        doTestFatal(locale, filename);
        doTestError(locale, filename);
        doTestWarn(locale, filename);
        doTestInfo(locale, filename);
        doTestDebug(locale, filename);
        doTestTrace(locale, filename);
    }

    protected void doTestFatal(Locale locale, String filename) throws Exception {
        before(Level.FATAL, getLocale(), filename);
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        LogMessages.LOGGER.couldNotDeleteFile("path", new Exception("Sluggo says"));
        Assertions.assertEquals("", baos.toString());
        after();
    }

    protected void doTestError(Locale locale, String filename) throws Exception {
        before(Level.ERROR, getLocale(), filename);

        // ERROR
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        LOG.info("actual: " + baos.toString());
        String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("oh no mr bill"));
        baos.reset();

        // WARN
        LogMessages.LOGGER.couldNotDeleteFile("path3", new Exception("Sluggo says"));
        Assertions.assertEquals("", baos.toString());
        after();
    }

    protected void doTestWarn(Locale locale, String filename) throws Exception {
        before(Level.WARN, locale, filename);

        // ERROR
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("oh no mr bill"));
        baos.reset();

        // WARN
        LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
        expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("Sluggo says"));
        baos.reset();

        // INFO
        LogMessages.LOGGER.deployingApplication("class", getClass());
        Assertions.assertEquals("", baos.toString());
        after();
    }

    protected void doTestInfo(Locale locale, String filename) throws Exception {
        before(Level.INFO, locale, filename);

        // ERROR
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("oh no mr bill"));
        baos.reset();

        // WARN
        LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
        expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("Sluggo says"));
        baos.reset();

        // INFO
        LogMessages.LOGGER.deployingApplication("className", getClass());
        expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
        Assertions.assertTrue(baos.toString().contains(expected));
        baos.reset();

        // DEBUG
        LogMessages.LOGGER.creatingContextObject("key", "value");
        Assertions.assertEquals("", baos.toString());
        after();
    }

    protected void doTestDebug(Locale locale, String filename) throws Exception {
        before(Level.DEBUG, locale, filename);

        // ERROR
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("oh no mr bill"));
        baos.reset();

        // WARN
        LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
        expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("Sluggo says"));
        baos.reset();

        // INFO
        LogMessages.LOGGER.deployingApplication("className", getClass());
        expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
        Assertions.assertTrue(baos.toString().contains(expected));
        baos.reset();

        // DEBUG
        LogMessages.LOGGER.creatingContextObject("key", "value");
        expected = getExpected(BASE + "300", "creatingContextObject", "key", "value");
        Assertions.assertTrue(baos.toString().contains(expected));
        after();
    }

    protected void doTestTrace(Locale locale, String filename) throws Exception {
        before(Level.TRACE, locale, filename);

        // ERROR
        LogMessages.LOGGER.failedExecutingError("method", "path", new Exception("oh no mr bill"));
        String expected = getExpected(BASE + "005", "failedExecutingError", "method", "path");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("oh no mr bill"));
        baos.reset();

        // WARN
        LogMessages.LOGGER.couldNotDeleteFile("file", new Exception("Sluggo says"));
        expected = getExpected(BASE + "125", "couldNotDeleteFile", "file");
        Assertions.assertTrue(baos.toString().contains(expected));
        Assertions.assertTrue(baos.toString().contains("java.lang.Exception"));
        Assertions.assertTrue(baos.toString().contains("Sluggo says"));
        baos.reset();

        // INFO
        LogMessages.LOGGER.deployingApplication("className", getClass());
        expected = getExpected(BASE + "225", "deployingApplication", "className", getClass());
        Assertions.assertTrue(baos.toString().contains(expected));
        baos.reset();

        // DEBUG
        LogMessages.LOGGER.creatingContextObject("key", "value");
        expected = getExpected(BASE + "300", "creatingContextObject", "key", "value");
        Assertions.assertTrue(baos.toString().contains(expected));
        after();
    }

    @Override
    protected int getExpectedNumberOfMethods() {
        LOG.info("expected number of methods: " + LogMessages.class.getDeclaredMethods().length);
        return LogMessages.class.getDeclaredMethods().length;
    }

    protected abstract Locale getLocale();
}
