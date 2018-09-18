package org.jboss.resteasy.tracing;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingConfig;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;

public class RESTEasyTracingUtils {
    static final List<String> SUMMARY_HEADERS = new ArrayList<>();

    static {
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_ENCODING.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_CHARSET.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.ACCEPT_LANGUAGE.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.CONTENT_TYPE.toLowerCase());
        SUMMARY_HEADERS.add(HttpHeaders.CONTENT_LENGTH.toLowerCase());
    }

    private RESTEasyTracingUtils() {
    }

    static String getTracingThreshold(RESTEasyTracingLevel appThreshold, HttpRequest request) {
        final String thresholdText = getHeaderString(request, RESTEasyTracing.HEADER_THRESHOLD);
        return (thresholdText == null) ? appThreshold.toString() : thresholdText;
    }

    static String getHeaderString(HttpRequest request, String header) {
        if (request == null) {
            return null;
        }
        if (request.getHttpHeaders() != null) {
            return request.getHttpHeaders().getHeaderString(header);
        }
        return null;
    }

    static String toStringOrNA(Object object) {
        if (object == null) {
            return "n/a";
        } else {
            return String.valueOf(object);
        }
    }
    
    /**
     * Test if application and request settings enabled tracing support.
     *
     * @param type    application tracing configuration type.
     * @param request request instance to check request headers.
     * @return {@code true} if tracing support is switched on for the request.
     */
    static boolean isTracingSupportEnabled(RESTEasyTracingConfig type, HttpRequest request) {
        return (type == RESTEasyTracingConfig.ALL)
                || ((type == RESTEasyTracingConfig.ON_DEMAND) && (getHeaderString(request, RESTEasyTracing.HEADER_ACCEPT) != null));
    }

    /**
     * Return configuration type of tracing support according to application configuration.
     * <p>
     * By default tracing support is switched OFF.
     */
    static RESTEasyTracingConfig getRESTEasyTracingConfig(Configuration configuration) {
        final Object tracingText = configuration.getProperty(ResteasyContextParameters.RESTEASY_TRACING_TYPE);
        final RESTEasyTracingConfig result;

        if (tracingText != null) {
            result = RESTEasyTracingConfig.valueOf((String) tracingText);
        } else {
            result = RESTEasyTracingConfig.OFF;
        }
        return result;
    }
    
    /**
     * Get request header specified JDK logger name suffix.
     *
     * @param request container request instance to get request header {@link RESTEasyTracing#HEADER_LOGGER} value.
     * @return Logger name suffix or {@code null} if not set.
     */
    static String getTracingLoggerNameSuffix(HttpRequest request) {
        return getHeaderString(request, RESTEasyTracing.HEADER_LOGGER);
    }

    /**
     * Get application-wide tracing level threshold.
     *
     * @return tracing level threshold.
     */
    static RESTEasyTracingLevel getRESTEasyTracingThreshold(Configuration configuration) {
        final Object thresholdText = configuration.getProperty(ResteasyContextParameters.RESTEASY_TRACING_THRESHOLD);
        return (thresholdText == null) ? RESTEasyTracing.DEFAULT_LEVEL : RESTEasyTracingLevel.valueOf((String) thresholdText);
    }


    protected static String getTracingInfoFormat(HttpRequest request) {
        return getHeaderString(request, RESTEasyTracing.HEADER_ACCEPT_FORMAT);
    }


}
