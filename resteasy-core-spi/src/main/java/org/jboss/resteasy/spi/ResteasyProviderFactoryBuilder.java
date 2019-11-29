package org.jboss.resteasy.spi;

public interface ResteasyProviderFactoryBuilder
{
   ResteasyProviderFactory newInstance(boolean registerBuiltin);
}
