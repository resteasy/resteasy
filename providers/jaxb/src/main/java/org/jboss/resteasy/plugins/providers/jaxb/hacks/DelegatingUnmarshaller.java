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

package org.jboss.resteasy.plugins.providers.jaxb.hacks;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.UnmarshallerHandler;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DelegatingUnmarshaller implements Unmarshaller {
    private final Unmarshaller delegate;

    DelegatingUnmarshaller(final Unmarshaller delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object unmarshal(final File f) throws JAXBException {
        return delegate.unmarshal(f);
    }

    @Override
    public Object unmarshal(final InputStream is) throws JAXBException {
        return delegate.unmarshal(is);
    }

    @Override
    public Object unmarshal(final Reader reader) throws JAXBException {
        return delegate.unmarshal(reader);
    }

    @Override
    public Object unmarshal(final URL url) throws JAXBException {
        return delegate.unmarshal(url);
    }

    @Override
    public Object unmarshal(final InputSource source) throws JAXBException {
        return delegate.unmarshal(source);
    }

    @Override
    public Object unmarshal(final Node node) throws JAXBException {
        return delegate.unmarshal(node);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(final Node node, final Class<T> declaredType) throws JAXBException {
        return delegate.unmarshal(node, declaredType);
    }

    @Override
    public Object unmarshal(final Source source) throws JAXBException {
        return delegate.unmarshal(source);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(final Source source, final Class<T> declaredType) throws JAXBException {
        return delegate.unmarshal(source, declaredType);
    }

    @Override
    public Object unmarshal(final XMLStreamReader reader) throws JAXBException {
        return delegate.unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(final XMLStreamReader reader,
                                        final Class<T> declaredType) throws JAXBException {
        return delegate.unmarshal(reader, declaredType);
    }

    @Override
    public Object unmarshal(final XMLEventReader reader) throws JAXBException {
        return delegate.unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(final XMLEventReader reader,
                                        final Class<T> declaredType) throws JAXBException {
        return delegate.unmarshal(reader, declaredType);
    }

    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return delegate.getUnmarshallerHandler();
    }

    @Override
    public void setValidating(final boolean validating) throws JAXBException {
        delegate.setValidating(validating);
    }

    @Override
    public boolean isValidating() throws JAXBException {
        return delegate.isValidating();
    }

    @Override
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        delegate.setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return delegate.getEventHandler();
    }

    @Override
    public void setProperty(final String name, final Object value) throws PropertyException {
        try {
            delegate.setProperty(name, value);
            return;
        } catch (PropertyException e) {
            LogMessages.LOGGER.debugf(e, "Failed to set %s with value %s", name, value);
        }
        final String newName = PropertyResolver.resolveProperty(name);
        if (newName != null) {
            try {
                delegate.setProperty(newName, value);
                return;
            } catch (PropertyException e) {
                LogMessages.LOGGER.debugf(e, "Failed to set %s with value %s", newName, value);
            }
        } else {
            throw Messages.MESSAGES.couldNotAddProperty(name, value);
        }
        throw Messages.MESSAGES.couldNotAddProperty(name, newName, value);
    }

    @Override
    public Object getProperty(final String name) throws PropertyException {
        try {
            return delegate.getProperty(name);
        } catch (PropertyException e) {
            LogMessages.LOGGER.debugf(e, "Failed to get property %s.", name);
        }
        final String newName = PropertyResolver.resolveProperty(name);
        if (newName != null) {
            try {
                return delegate.getProperty(newName);
            } catch (PropertyException e) {
                LogMessages.LOGGER.debugf(e, "Failed to get property %s.", name);
            }
        } else {
            throw Messages.MESSAGES.couldNotGetProperty(name);
        }
        throw Messages.MESSAGES.couldNotGetProperty(name, newName);
    }

    @Override
    public void setSchema(final Schema schema) {
        delegate.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return delegate.getSchema();
    }

    @Override
    public void setAdapter(final XmlAdapter adapter) {
        delegate.setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        delegate.setAdapter(type, adapter);
    }

    @Override
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        return delegate.getAdapter(type);
    }

    @Override
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller au) {
        delegate.setAttachmentUnmarshaller(au);
    }

    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return delegate.getAttachmentUnmarshaller();
    }

    @Override
    public void setListener(final Listener listener) {
        delegate.setListener(listener);
    }

    @Override
    public Listener getListener() {
        return delegate.getListener();
    }
}
