/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.plugins.providers.jsonb;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ManagedJsonb implements Jsonb {
    private final Object lock = new Object();
    private final boolean unmanaged;
    // Guarded by lock
    private Jsonb delegate;

    ManagedJsonb(final Jsonb delegate) {
        this.delegate = delegate;
        unmanaged = delegate == null;
    }

    @Override
    public <T> T fromJson(final String str, final Class<T> type) throws JsonbException {
        return getDelegate().fromJson(str, type);
    }

    @Override
    public <T> T fromJson(final String str, final Type runtimeType) throws JsonbException {
        return getDelegate().fromJson(str, runtimeType);
    }

    @Override
    public <T> T fromJson(final Reader reader, final Class<T> type) throws JsonbException {
        return getDelegate().fromJson(reader, type);
    }

    @Override
    public <T> T fromJson(final Reader reader, final Type runtimeType) throws JsonbException {
        return getDelegate().fromJson(reader, runtimeType);
    }

    @Override
    public <T> T fromJson(final InputStream stream, final Class<T> type) throws JsonbException {
        return getDelegate().fromJson(stream, type);
    }

    @Override
    public <T> T fromJson(final InputStream stream, final Type runtimeType) throws JsonbException {
        return getDelegate().fromJson(stream, runtimeType);
    }

    @Override
    public String toJson(final Object object) throws JsonbException {
        return getDelegate().toJson(object);
    }

    @Override
    public String toJson(final Object object, final Type runtimeType) throws JsonbException {
        return getDelegate().toJson(object, runtimeType);
    }

    @Override
    public void toJson(final Object object, final Writer writer) throws JsonbException {
        getDelegate().toJson(object, writer);
    }

    @Override
    public void toJson(final Object object, final Type runtimeType, final Writer writer) throws JsonbException {
        getDelegate().toJson(object, runtimeType, writer);
    }

    @Override
    public void toJson(final Object object, final OutputStream stream) throws JsonbException {
        getDelegate().toJson(object, stream);
    }

    @Override
    public void toJson(final Object object, final Type runtimeType, final OutputStream stream) throws JsonbException {
        getDelegate().toJson(object, runtimeType, stream);
    }

    @Override
    public void close() throws Exception {
        if (unmanaged) {
            synchronized (lock) {
                if (delegate != null) {
                    delegate.close();
                    delegate = null;
                }
            }
        }
    }

    private Jsonb getDelegate() {
        Jsonb result = delegate;
        if (unmanaged) {
            synchronized (lock) {
                if (result == null) {
                    result = delegate = JsonbBuilder.create();
                }
            }
        }
        return result;
    }
}
