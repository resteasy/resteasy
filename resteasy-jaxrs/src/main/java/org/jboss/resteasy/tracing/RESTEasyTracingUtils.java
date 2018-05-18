package org.jboss.resteasy.tracing;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

public class RESTEasyTracingUtils {
    private static final List<String> SUMMARY_HEADERS = new ArrayList<>();

    static {
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_ENCODING.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_CHARSET.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_LANGUAGE.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.CONTENT_TYPE.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.CONTENT_LENGTH.toLowerCase());
    }

    private static final RESTEasyTracingConfig DEFAULT_CONFIGURATION_TYPE = RESTEasyTracingConfig.OFF;

    private RESTEasyTracingUtils() {
    }

    /**
     * According to configuration/request header it initialize {@link RESTEasyTracingLogger} and put it to the request properties.
     *
     * @param type         application-wide tracing configuration type.
     * @param appThreshold application-wide tracing level threshold.
     * @param request      request instance to get runtime properties to store {@link RESTEasyTracingLogger} instance to
     *                     if tracing support is enabled for the request.
     */
    public static void initTracingSupport(RESTEasyTracingConfig type,
                                          RESTEasyTracingLevel appThreshold,
                                          HttpRequest request) {
        final RESTEasyTracingLogger tracingLogger;
        if (isTracingSupportEnabled(type, request)) {
            tracingLogger = RESTEasyTracingLogger.create(
                    getTracingThreshold(appThreshold, request),
                    getTracingLoggerNameSuffix(request));
        } else {
            tracingLogger = RESTEasyTracingLogger.empty();
        }

        request.setAttribute(RESTEasyTracingLogger.PROPERTY_NAME, tracingLogger);
    }

    /**
     * Log tracing messages START events.
     *
     * @param request container request instance to get runtime properties
     *                to check if tracing support is enabled for the request.
     */
    public static void logStart(HttpRequest request) {
        if (request == null) {
            return;
        }
        RESTEasyTracingLogger tracingLogger = RESTEasyTracingLogger.getInstance(null);
        if (tracingLogger.isLogEnabled(RESTEasyServerTracingEvent.START)) {
            StringBuilder text = new StringBuilder();
            SecurityContext securityContext = ResteasyProviderFactory.getContextData(SecurityContext.class);
            text.append(String.format("baseUri=[%s] requestUri=[%s] method=[%s] authScheme=[%s]",
                    request.getUri().getBaseUri(), request.getUri().getRequestUri(), request.getHttpMethod(),
                    toStringOrNA(securityContext == null ? null : securityContext.getAuthenticationScheme())));
            for (String header : SUMMARY_HEADERS) {
                text.append(String.format(" %s=%s", header, toStringOrNA(getHeaderString(request, header))));
            }
            tracingLogger.log(RESTEasyServerTracingEvent.START, text.toString());
        }
        if (tracingLogger.isLogEnabled(RESTEasyServerTracingEvent.START_HEADERS)) {
            StringBuilder text = new StringBuilder();
            HttpHeaders headers = request.getHttpHeaders();
            if (headers != null) {
                for (String header : headers.getRequestHeaders().keySet()) {
                    if (!SUMMARY_HEADERS.contains(header)) {
                        text.append(String.format(" %s=%s", header, toStringOrNA(headers.getRequestHeaders().get(header))));
                    }
                }
                if (text.length() > 0) {
                    text.insert(0, "Other request headers:");
                }
                tracingLogger.log(RESTEasyServerTracingEvent.START_HEADERS, text.toString());
            }
        }
    }

    /**
     * Test if application and request settings enabled tracing support.
     *
     * @param type    application tracing configuration type.
     * @param request request instance to check request headers.
     * @return {@code true} if tracing support is switched on for the request.
     */
    private static boolean isTracingSupportEnabled(RESTEasyTracingConfig type, HttpRequest request) {
        return (type == RESTEasyTracingConfig.ALL)
                || ((type == RESTEasyTracingConfig.ON_DEMAND) && (getHeaderString(request, RESTEasyTracingLogger.HEADER_ACCEPT) != null));
    }

    /**
     * Return configuration type of tracing support according to application configuration.
     * <p>
     * By default tracing support is switched OFF.
     */
    public static RESTEasyTracingConfig getTracingConfig(Configuration configuration) {
        final Object tracingText = configuration.getProperty(ResteasyContextParameters.RESTEASY_TRACING_TYPE);
        final RESTEasyTracingConfig result;

        if (tracingText != null) {
            result = RESTEasyTracingConfig.valueOf((String) tracingText);
        } else {
            result = DEFAULT_CONFIGURATION_TYPE;
        }
        return result;
    }

    /**
     * Get request header specified JDK logger name suffix.
     *
     * @param request container request instance to get request header {@link RESTEasyTracingLogger#HEADER_LOGGER} value.
     * @return Logger name suffix or {@code null} if not set.
     */
    private static String getTracingLoggerNameSuffix(HttpRequest request) {
        return getHeaderString(request, RESTEasyTracingLogger.HEADER_LOGGER);
    }

    /**
     * Get application-wide tracing level threshold.
     *
     * @return tracing level threshold.
     */
    public static RESTEasyTracingLevel getTracingThreshold(Configuration configuration) {
        final Object thresholdText = configuration.getProperty(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD);
        return (thresholdText == null) ? RESTEasyTracingLogger.DEFAULT_LEVEL : RESTEasyTracingLevel.valueOf((String) thresholdText);
    }

    private static RESTEasyTracingLevel getTracingThreshold(RESTEasyTracingLevel appThreshold, HttpRequest request) {
        final String thresholdText = getHeaderString(request, RESTEasyTracingLogger.HEADER_THRESHOLD);
        return (thresholdText == null) ? appThreshold : RESTEasyTracingLevel.valueOf(thresholdText);
    }

    private static String getHeaderString(HttpRequest request, String header) {
        if (request == null) {
            return null;
        }
        if (request.getHttpHeaders() != null) {
            return request.getHttpHeaders().getHeaderString(header);
        }
        return null;
    }

    private static String toStringOrNA(Object object) {
        if (object == null) {
            return "n/a";
        } else {
            return String.valueOf(object);
        }
    }


}
