package org.jboss.resteasy.tracing;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.api.RESTEasyTracing;
import org.jboss.resteasy.tracing.api.RESTEasyTracingConfig;
import org.jboss.resteasy.tracing.api.RESTEasyTracingLevel;

public interface RESTEasyTracingLogger
{
   class TRACING {
      public static final boolean AVAILABLE;
      static {
         boolean b;
         try {
            Class.forName("org.jboss.resteasy.tracing.api.RESTEasyTracing");
            b = true;
         } catch (Throwable t) {
            b = false;
         }
         AVAILABLE = b;
      }
   }

   boolean isLogEnabled(String eventName);

   void log(String eventName, Object... args);

   void logDuration(String eventName, long fromTimestamp, Object... args);

   long timestamp(String eventName);

   /**
    * Stores collected tracing messages to response HTTP header.
    *
    * @param headers message headers.
    */
   void flush(MultivaluedMap<String, Object> headers);

    /**
     * Create new Tracing logger.
     *
     * @param threshold        tracing level threshold.
     * @param loggerNameSuffix tracing logger name suffix.
     * @return new tracing logger.
     */
    static RESTEasyTracingLogger create(final String threshold, final String loggerNameSuffix) {
        return create(threshold, loggerNameSuffix, null);
    }


    static RESTEasyTracingLogger create(String tracingThreshold, String tracingLoggerNameSuffix, String tracingInfoFormat) {
        if (!TRACING.AVAILABLE) {
            return EMPTY;
        }
        return new RESTEasyTracingLoggerImpl(RESTEasyTracingLevel.valueOf(tracingThreshold), tracingLoggerNameSuffix, tracingInfoFormat);
    }

   /**
    * Create new Tracing logger.
    *
    * @param Configuration        configuration
    * @param loggerNameSuffix tracing logger name suffix.
    * @return new tracing logger.
    */
   static RESTEasyTracingLogger create(final Configuration configuration, final String loggerNameSuffix)
   {
      if (!TRACING.AVAILABLE)
      {
         return EMPTY;
      }
      return new RESTEasyTracingLoggerImpl(RESTEasyTracingUtils.getRESTEasyTracingThreshold(configuration), loggerNameSuffix);
   }

   /**
    * Returns instance of {@code TracingLogger} associated with current request processing
    * ({@code propertiesDelegate}).
    *
    * @return returns instance of {@code TracingLogger} from {@code ResteasyProviderFactory}. Does not return {@code null}.
    */
   static RESTEasyTracingLogger getInstance(HttpRequest request)
   {
      if (request == null || !TRACING.AVAILABLE)
      {
         return EMPTY;
      }

      final RESTEasyTracingLogger tracingLogger = (RESTEasyTracingLogger) request.getAttribute(RESTEasyTracing.PROPERTY_NAME);

      return tracingLogger == null ? EMPTY : tracingLogger;
   }

   static RESTEasyTracingLogger empty()
   {
      return EMPTY;
   }

   RESTEasyTracingLogger EMPTY = new RESTEasyTracingLogger()
   {

      @Override
      public void flush(final MultivaluedMap<String, Object> headers)
      {
         // no-op
      }

      @Override
      public boolean isLogEnabled(String eventName)
      {
         return false;
      }

      @Override
      public void log(String eventName, Object... args)
      {
         // no-op
      }

      @Override
      public void logDuration(String eventName, long fromTimestamp, Object... args)
      {
         // no-op
      }

      @Override
      public long timestamp(String eventName)
      {
         return 0;
      }
   };

    /**
     * According to configuration/request header it initialize {@link RESTEasyTracingLogger} and put it to the request properties.
     *
     * @param configuration application-wide tracing configuration type and tracing level threshold.
     * @param request       request instance to get runtime properties to store {@link RESTEasyTracingLogger} instance to
     *                      if tracing support is enabled for the request.
     */
   static void initTracingSupport(Configuration configuration,
                                         HttpRequest request) {
       if (!TRACING.AVAILABLE || request.getAttribute(RESTEasyTracing.PROPERTY_NAME) != null)
           return;

       final RESTEasyTracingLogger tracingLogger;
       if (RESTEasyTracingUtils.isTracingSupportEnabled(RESTEasyTracingUtils.getRESTEasyTracingConfig(configuration), request)) {
           tracingLogger = RESTEasyTracingLogger.create(
                 RESTEasyTracingUtils.getTracingThreshold(RESTEasyTracingUtils.getRESTEasyTracingThreshold(configuration), request),
                 RESTEasyTracingUtils.getTracingLoggerNameSuffix(request),
                   RESTEasyTracingUtils.getTracingInfoFormat(request));
       } else {
           tracingLogger = RESTEasyTracingLogger.empty();
       }

       request.setAttribute(RESTEasyTracing.PROPERTY_NAME, tracingLogger);

   }

   /**
    * Log tracing messages START events.
    *
    * @param request container request instance to get runtime properties
    *                to check if tracing support is enabled for the request.
    */
   static void logStart(HttpRequest request) {
       if (!TRACING.AVAILABLE || request == null) {
           return;
       }

       RESTEasyTracingLogger tracingLogger = RESTEasyTracingLogger.getInstance(request);
       if (tracingLogger.isLogEnabled("START")) {
           StringBuilder text = new StringBuilder();
           SecurityContext securityContext = ResteasyProviderFactory.getContextData(SecurityContext.class);
           text.append(String.format("baseUri=[%s] requestUri=[%s] method=[%s] authScheme=[%s]",
                   request.getUri().getBaseUri(), request.getUri().getRequestUri(), request.getHttpMethod(),
                   RESTEasyTracingUtils.toStringOrNA(securityContext == null ? null : securityContext.getAuthenticationScheme())));
           for (String header : RESTEasyTracingUtils.SUMMARY_HEADERS) {
               text.append(String.format(" %s=%s", header, RESTEasyTracingUtils.toStringOrNA(RESTEasyTracingUtils.getHeaderString(request, header))));
           }
           tracingLogger.log("START", text.toString());
       }
       if (tracingLogger.isLogEnabled("START_HEADERS")) {
           StringBuilder text = new StringBuilder();
           HttpHeaders headers = request.getHttpHeaders();
           if (headers != null) {
               for (String header : headers.getRequestHeaders().keySet()) {
                   if (!RESTEasyTracingUtils.SUMMARY_HEADERS.contains(header)) {
                       text.append(String.format(" %s=%s", header, RESTEasyTracingUtils.toStringOrNA(headers.getRequestHeaders().get(header))));
                   }
               }
               if (text.length() > 0) {
                   text.insert(0, "Other request headers:");
               }
               tracingLogger.log("START_HEADERS", text.toString());
           }
       }
   }


   static boolean isTracingConfigALL(Configuration configuration) {
      return TRACING.AVAILABLE && RESTEasyTracingUtils.getRESTEasyTracingConfig(configuration) == RESTEasyTracingConfig.ALL;
   }

   /**
    * Return configuration type of tracing support according to application configuration.
    * <p>
    * By default tracing support is switched OFF.
    */
   static String getTracingConfig(Configuration configuration) {
      return TRACING.AVAILABLE ? RESTEasyTracingUtils.getRESTEasyTracingConfig(configuration).toString() : null;
   }

   /**
    * Get application-wide tracing level threshold.
    *
    * @return tracing level threshold.
    */
   static String getTracingThreshold(Configuration configuration) {
      return TRACING.AVAILABLE ? RESTEasyTracingUtils.getRESTEasyTracingThreshold(configuration).toString() : null;
   }
}
