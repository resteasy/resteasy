package org.jboss.resteasy.spi;

/**
 * HttpRequestPreprocessors get invoked before any dispatching to JAX-RS resource methods happens
 */
public interface HttpRequestPreprocessor
{
   void preProcess(HttpRequest request);
}
