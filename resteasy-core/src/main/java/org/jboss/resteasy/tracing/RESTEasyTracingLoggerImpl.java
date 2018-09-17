package org.jboss.resteasy.tracing;

import org.jboss.logging.Logger;

import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingEvent;
import org.jboss.resteasy.tracing.api.RESTEasyTracingInfo;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;
import org.jboss.resteasy.tracing.api.RESTEasyServerTracingEvent;
import org.jboss.resteasy.tracing.api.RESTEasyMsgTraceEvent;
import org.jboss.resteasy.tracing.api.RESTEasyTracingMessage;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class RESTEasyTracingLoggerImpl extends RESTEasyTracing implements RESTEasyTracingLogger {

   private final Logger logger;
   private final RESTEasyTracingLevel threshold;
   private final RESTEasyTracingInfo tracingInfo;
   private static final Map<String, RESTEasyTracingEvent> string2event = new HashMap<>();

   static {
      for (RESTEasyTracingEvent v : RESTEasyServerTracingEvent.values()) {
         string2event.put(v.name(), v);
      }
      for (RESTEasyTracingEvent v : RESTEasyMsgTraceEvent.values()) {
         string2event.put(v.name(), v);
      }
   }

    RESTEasyTracingLoggerImpl(final RESTEasyTracingLevel threshold, String loggerNameSuffix) {
        this(threshold, loggerNameSuffix, null);
    }

    RESTEasyTracingLoggerImpl(final RESTEasyTracingLevel threshold, String loggerNameSuffix, String format) {
        this.threshold = threshold;
        loggerNameSuffix = loggerNameSuffix != null ? loggerNameSuffix : DEFAULT_LOGGER_NAME_SUFFIX;
        this.logger = Logger.getLogger(TRACING_LOGGER_NAME_PREFIX + "." + loggerNameSuffix);
        tracingInfo = RESTEasyTracingInfo.get(format);
    }

    private RESTEasyTracingEvent getEvent(String eventName) {
      RESTEasyTracingEvent e = string2event.get(eventName);
      if (e == null) {
         throw new IllegalArgumentException();
      }
      return e;
   }

    @Override
    public boolean isLogEnabled(String eventName) {
       return isLogEnabled(getEvent(eventName));
    }

    @Override
    public void log(String eventName, Object... args) {
       log(getEvent(eventName), args);
    }

    @Override
    public void logDuration(String eventName, long fromTimestamp, Object... args)
    {
       logDuration(getEvent(eventName), fromTimestamp, args);
    }

    @Override
    public long timestamp(String eventName)
    {
       return timestamp(getEvent(eventName));
    }

    private boolean isEnabled(final RESTEasyTracingLevel level) {
        return threshold.ordinal() >= level.ordinal();
    }

    @Override
    public boolean isLogEnabled(final RESTEasyTracingEvent event) {
        return isEnabled(event.level());
    }

    @Override
    public void log(final RESTEasyTracingEvent event, final Object... args) {
        logDuration(event, -1, args);
    }

    @Override
    public void logDuration(final RESTEasyTracingEvent event, final long fromTimestamp, final Object... args) {
        if (isEnabled(event.level())) {
            final long toTimestamp;
            if (fromTimestamp == -1) {
                toTimestamp = -1;
            } else {
                toTimestamp = System.nanoTime();
            }
            long duration = 0;
            if ((fromTimestamp != -1) && (toTimestamp != -1)) {
                duration = toTimestamp - fromTimestamp;
            }
            logImpl(event, duration, args);
        }
    }

    private void logImpl(final RESTEasyTracingEvent event, final long duration, final Object... messageArgs) {
        if (isEnabled(event.level())) {
            final String[] messageArgsStr = new String[messageArgs.length];
            for (int i = 0; i < messageArgs.length; i++) {
                messageArgsStr[i] = formatInstance(messageArgs[i]);
            }
            final RESTEasyTracingMessage message = new RESTEasyTracingMessage(event, duration, messageArgsStr);
            tracingInfo.addMessage(message);

            final Logger.Level loggingLevel;
            switch (event.level()) {
                case SUMMARY:
                    loggingLevel = Logger.Level.INFO;
                    break;
                case TRACE:
                    loggingLevel = Logger.Level.DEBUG;
                    break;
                case VERBOSE:
                    loggingLevel = Logger.Level.TRACE;
                    break;
                default:
                    loggingLevel = Logger.Level.INFO;
            }
            if (logger.isEnabled(loggingLevel)) {
                logger.log(loggingLevel,
                        event.name() + ' ' + message.toString() + " [" + tracingInfo.formatDuration(duration) + " ms]");
            }
        }
    }

    private static void formatInstance(final Object instance, final StringBuilder text) {
        text.append(instance.getClass().getName()).append(" @")
                .append(Integer.toHexString(System.identityHashCode(instance)));
    }

    private static void formatResponse(final Response response, final StringBuilder text) {
        text.append(" <").append(formatStatusInfo(response.getStatusInfo())).append('|');
        if (response.hasEntity()) {
            formatInstance(response.getEntity(), text);
        } else {
            text.append("-no-entity-");
        }
        text.append('>');
    }

    private static String formatStatusInfo(final Response.StatusType statusInfo) {
        return String.valueOf(statusInfo.getStatusCode()) + '/' + statusInfo.getFamily() + '|' + statusInfo.getReasonPhrase();
    }

    private String formatInstance(Object instance) {
        final StringBuilder text = new StringBuilder();
        if (instance == null) {
            text.append("null");
        } else if ((instance instanceof Number) || (instance instanceof String) || (instance instanceof Method)) {
            text.append(instance.toString());
        } else if (instance instanceof Response.StatusType) {
            text.append(formatStatusInfo((Response.StatusType) instance));
        } else {
            text.append('[');
            formatInstance(instance, text);
            if (instance.getClass().isAnnotationPresent(Priority.class)) {
                text.append(" #").append(instance.getClass().getAnnotation(Priority.class).value());
            }
            if (instance instanceof WebApplicationException) {
                formatResponse(((WebApplicationException) instance).getResponse(), text);
            } else if (instance instanceof Response) {
                formatResponse(((Response) instance), text);
            }
            text.append(']');
        }
        return text.toString();
    }

    @Override
    public long timestamp(final RESTEasyTracingEvent event) {
        if (isEnabled(event.level())) {
            return System.nanoTime();
        }
        return -1;
    }

    @Override
    public void flush(final MultivaluedMap<String, Object> headers) {
        final String[] messages = tracingInfo.getMessages();
        for (int i = 0; i < messages.length; i++) {
            headers.putSingle(String.format(RESTEasyTracing.HEADER_RESPONSE_FORMAT, i), messages[i]);
        }
    }
}
