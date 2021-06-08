package org.jboss.resteasy.specimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.net.ssl.SSLContext;

import jakarta.ws.rs.SeBootstrap;

public class BootstrapConfigurationBuilderImpl implements SeBootstrap.Configuration.Builder {
    private Map<String, Object> properties = new HashMap<String, Object>();
    @SuppressWarnings("rawtypes")
    private BiFunction propertiesProvider;

    @Override
    public SeBootstrap.Configuration build() {
        return null;
    }

    @Override
    public SeBootstrap.Configuration.Builder property(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public SeBootstrap.Configuration.Builder protocol(String protocol) {
        return SeBootstrap.Configuration.Builder.super.protocol(protocol);
    }

    @Override
    public SeBootstrap.Configuration.Builder host(String host) {
        return SeBootstrap.Configuration.Builder.super.host(host);
    }

    @Override
    public SeBootstrap.Configuration.Builder port(Integer port) {
        return SeBootstrap.Configuration.Builder.super.port(port);
    }

    @Override
    public SeBootstrap.Configuration.Builder rootPath(String rootPath) {
        return SeBootstrap.Configuration.Builder.super.rootPath(rootPath);
    }

    @Override public SeBootstrap.Configuration.Builder sslContext(SSLContext sslContext) {
        return SeBootstrap.Configuration.Builder.super.sslContext(sslContext);
    }

    @Override
    public SeBootstrap.Configuration.Builder sslClientAuthentication(
            SeBootstrap.Configuration.SSLClientAuthentication sslClientAuthentication) {
        return SeBootstrap.Configuration.Builder.super.sslClientAuthentication(sslClientAuthentication);
    }

    @Override
    public <T> SeBootstrap.Configuration.Builder from(BiFunction<String, Class<T>, Optional<T>> propertiesProvider) {
        Objects.requireNonNull(propertiesProvider);
        this.propertiesProvider = propertiesProvider;
        return this;
    }

    @Override
    public SeBootstrap.Configuration.Builder from(Object externalConfig) {
        return SeBootstrap.Configuration.Builder.super.from(externalConfig);
    }
    private class ServerConfiguration implements jakarta.ws.rs.SeBootstrap.Configuration {
        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Object property(String name)
        {
            Object result = properties.get(name);
            if (result == null && propertiesProvider != null)
            {
                result = propertiesProvider.apply(name, Object.class);
                return ((Optional)result).isPresent() ? ((Optional)result).get() : null;
            }
            return result;
        }
    }

}
