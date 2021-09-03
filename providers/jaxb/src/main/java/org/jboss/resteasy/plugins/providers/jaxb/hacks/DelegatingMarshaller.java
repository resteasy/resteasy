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
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DelegatingMarshaller implements Marshaller {
    private final Marshaller delegate;

    DelegatingMarshaller(final Marshaller delegate) {
        this.delegate = delegate;
    }

    @Override
    public void marshal(final Object jaxbElement, final Result result) throws JAXBException {
        delegate.marshal(jaxbElement, result);
    }

    @Override
    public void marshal(final Object jaxbElement, final OutputStream os) throws JAXBException {
        delegate.marshal(jaxbElement, os);
    }

    @Override
    public void marshal(final Object jaxbElement, final File output) throws JAXBException {
        delegate.marshal(jaxbElement, output);
    }

    @Override
    public void marshal(final Object jaxbElement, final Writer writer) throws JAXBException {
        delegate.marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(final Object jaxbElement, final ContentHandler handler) throws JAXBException {
        delegate.marshal(jaxbElement, handler);
    }

    @Override
    public void marshal(final Object jaxbElement, final Node node) throws JAXBException {
        delegate.marshal(jaxbElement, node);
    }

    @Override
    public void marshal(final Object jaxbElement, final XMLStreamWriter writer) throws JAXBException {
        delegate.marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(final Object jaxbElement, final XMLEventWriter writer) throws JAXBException {
        delegate.marshal(jaxbElement, writer);
    }

    @Override
    public Node getNode(final Object contentTree) throws JAXBException {
        return delegate.getNode(contentTree);
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
    public void setEventHandler(final ValidationEventHandler handler) throws JAXBException {
        delegate.setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return delegate.getEventHandler();
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
    public void setAttachmentMarshaller(final AttachmentMarshaller am) {
        delegate.setAttachmentMarshaller(am);
    }

    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return delegate.getAttachmentMarshaller();
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
    public void setListener(final Listener listener) {
        delegate.setListener(listener);
    }

    @Override
    public Listener getListener() {
        return delegate.getListener();
    }
}
