package org.jboss.resteasy.tracing;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingEvent;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;

import javax.ws.rs.core.MultivaluedMap;

public abstract class RESTEasyTracingLogger extends RESTEasyTracing {

   /**
     * Test if a tracing support is enabled if {@code event} can be logged (according to event.level and threshold level set).
     *
     * @param event event type to be tested
     * @return {@code true} if {@code event} can be logged
     */
    public abstract boolean isLogEnabled(RESTEasyTracingEvent event);

    /**
     * Try to log event according to event level and request context threshold level setting.
     *
     * @param event event type to be logged
     * @param args  message arguments (in relation to {@link RESTEasyTracingEvent#messageFormat()}
     */
    public abstract void log(RESTEasyTracingEvent event, Object... args);

    /**
     * Try to log event according to event level and request context threshold level setting.
     * <p>
     * If logging support is switched on for current request and event setting the method computes duration of event and log
     * message. If {@code fromTimestamp} is not set (i.e. {@code -1}) then duration of event
     * is {@code 0}.
     *
     * @param event         event type to be logged
     * @param fromTimestamp logged event is running from the timestamp in nanos. {@code -1} in case event has no duration
     * @param args          message arguments (in relation to {@link RESTEasyTracingEvent#messageFormat()#messageFormat()}
     */
    public abstract void logDuration(RESTEasyTracingEvent event, long fromTimestamp, Object... args);

    /**
     * If logging support is switched on for current request and event setting the method returns current timestamp in nanos.
     *
     * @param event event type to be logged
     * @return Current timestamp in nanos or {@code -1} if tracing is not enabled
     */
    public abstract long timestamp(RESTEasyTracingEvent event);

    /**
     * Stores collected tracing messages to response HTTP header.
     *
     * @param headers message headers.
     */
    public abstract void flush(MultivaluedMap<String, Object> headers);

    /**
     * Create new Tracing logger.
     *
     * @param threshold        tracing level threshold.
     * @param loggerNameSuffix tracing logger name suffix.
     * @return new tracing logger.
     */
    public static RESTEasyTracingLogger create(final RESTEasyTracingLevel threshold, final String loggerNameSuffix) {
        return new RESTEasyTracingLoggerImpl(threshold, loggerNameSuffix);
    }

    /**
     * Returns instance of {@code TracingLogger} associated with current request processing
     * ({@code propertiesDelegate}).
     *
     * @return returns instance of {@code TracingLogger} from {@code ResteasyProviderFactory}. Does not return {@code null}.
     */
    public static RESTEasyTracingLogger getInstance(HttpRequest request) {
        if (request == null) {
            return EMPTY;
        }

        final RESTEasyTracingLogger tracingLogger = (RESTEasyTracingLogger) request.getAttribute(PROPERTY_NAME);

        return tracingLogger == null ? EMPTY : tracingLogger;
    }


    public static RESTEasyTracingLogger empty() {
        return EMPTY;
    }

    private static final RESTEasyTracingLogger EMPTY = new RESTEasyTracingLogger() {

        @Override
        public boolean isLogEnabled(final RESTEasyTracingEvent event) {
            return false;
        }

        @Override
        public void log(final RESTEasyTracingEvent event, final Object... args) {
            // no-op
        }

        @Override
        public void logDuration(final RESTEasyTracingEvent event, final long fromTimestamp, final Object... args) {
            // no-op
        }

        @Override
        public long timestamp(final RESTEasyTracingEvent event) {
            return -1;
        }

        @Override
        public void flush(final MultivaluedMap<String, Object> headers) {
            // no-op
        }
    };
}
