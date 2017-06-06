package org.jboss.resteasy.client.jaxrs;

public interface ClientHttpEngineBuilder
{
   public ClientHttpEngineBuilder resteasyClientBuilder(ResteasyClientBuilder resteasyClientBuilder);

   public ClientHttpEngine build();
}
