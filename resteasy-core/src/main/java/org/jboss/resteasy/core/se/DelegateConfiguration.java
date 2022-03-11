/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.core.se;

import java.net.URI;
import javax.net.ssl.SSLContext;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DelegateConfiguration implements SeBootstrap.Configuration {
    private final SeBootstrap.Configuration delegate;
    private final SeBootstrap.Configuration defaults;

    DelegateConfiguration(final SeBootstrap.Configuration delegate, final SeBootstrap.Configuration defaults) {
        this.delegate = delegate;
        this.defaults = defaults;
    }

    @Override
    public Object property(final String name) {
        if (delegate.hasProperty(name)) {
            return delegate.property(name);
        }
        return defaults.property(name);
    }

    @Override
    public boolean hasProperty(final String name) {
        return delegate.hasProperty(name) || delegate.hasProperty(name);
    }

    @Override
    public String protocol() {
        if (delegate.hasProperty(PROTOCOL)) {
            return delegate.protocol();
        }
        return defaults.protocol();
    }

    @Override
    public String host() {
        if (delegate.hasProperty(HOST)) {
            return delegate.host();
        }
        return defaults.host();
    }

    @Override
    public int port() {
        if (delegate.hasProperty(PORT)) {
            return delegate.port();
        }
        return defaults.port();
    }

    @Override
    public String rootPath() {
        if (delegate.hasProperty(ROOT_PATH)) {
            return delegate.rootPath();
        }
        return defaults.rootPath();
    }

    @Override
    public SSLContext sslContext() {
        if (delegate.hasProperty(SSL_CONTEXT)) {
            return delegate.sslContext();
        }
        return defaults.sslContext();
    }

    @Override
    public SSLClientAuthentication sslClientAuthentication() {
        if (delegate.hasProperty(SSL_CLIENT_AUTHENTICATION)) {
            return delegate.sslClientAuthentication();
        }
        return defaults.sslClientAuthentication();
    }

    @Override
    public UriBuilder baseUriBuilder() {
        return delegate.baseUriBuilder();
    }

    @Override
    public URI baseUri() {
        return delegate.baseUri();
    }
}
