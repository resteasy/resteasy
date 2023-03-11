package org.jboss.resteasy.spi;

import java.lang.reflect.Method;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.statistics.MethodStatisticsLogger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceInvoker {
    Response invoke(HttpRequest request, HttpResponse response);

    Response invoke(HttpRequest request, HttpResponse response, Object target);

    Method getMethod();

    // optimizations
    boolean hasProduces();

    void setMethodStatisticsLogger(MethodStatisticsLogger msLogger);

    MethodStatisticsLogger getMethodStatisticsLogger();
}
