package org.jboss.resteasy.tracing;

import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.MultivaluedMap;

public abstract class RESTEasyTracingLogger {
    /**
     * {@code TracingLogger} instance is placed in request context properties under this name.
     */
    public static final String PROPERTY_NAME = RESTEasyTracingLogger.class.getName();
    /**
     * HTTP header prefix.
     */
    public static final String HEADER_TRACING_PREFIX = "X-RESTEasy-Tracing-";
    /**
     * Request header name to change application default tracing level.
     */
    public static final String HEADER_THRESHOLD = HEADER_TRACING_PREFIX + "Threshold";
    /**
     * Request header name to switch on request tracing.
     * Make sense in case of tracing support enabled by ON_DEMAND value.
     */
    public static final String HEADER_ACCEPT = HEADER_TRACING_PREFIX + "Accept";
    /**
     * Request header name to set JDK logger name suffix to identify a request logs.
     */
    public static final String HEADER_LOGGER = HEADER_TRACING_PREFIX + "Logger";
    /**
     * Response header name format.
     */
    protected static final String HEADER_RESPONSE_FORMAT = HEADER_TRACING_PREFIX + "%03d";
    /**
     * Default event level.
     */
    public static final RESTEasyTracingLevel DEFAULT_LEVEL = RESTEasyTracingLevel.TRACE;
    /**
     * JDK logger name prefix.
     */
    protected static final String TRACING_LOGGER_NAME_PREFIX = "org.jboss.resteasy.tracing";
    /**
     * Default JDK logger name suffix. This can be overwrite by header {@link #HEADER_LOGGER}.
     */
    protected static final String DEFAULT_LOGGER_NAME_SUFFIX = "general";

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
            //not server side
            return EMPTY;
        }
        final RESTEasyTracingLogger tracingLogger = (RESTEasyTracingLogger) request.getAttribute(PROPERTY_NAME);
        return (tracingLogger != null) ? tracingLogger : EMPTY;
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
