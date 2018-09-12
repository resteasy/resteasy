package org.jboss.resteasy.client.jaxrs;

public interface ClientHttpEngineBuilder
{
   ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder);

   ClientHttpEngine build();
}
