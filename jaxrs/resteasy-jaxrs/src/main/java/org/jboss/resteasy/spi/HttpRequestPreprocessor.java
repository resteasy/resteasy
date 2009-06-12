package org.jboss.resteasy.spi;

public interface HttpRequestPreprocessor
{
   void preProcess(HttpRequest request);
}
