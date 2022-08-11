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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;

/**
 * For internal use only.
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class RiHacks {

    public static Object createAtomNamespacePrefixMapper() throws JAXBException {
        return createNamespacePrefixMapper((namespace, suggestion) -> {
            if ("http://www.w3.org/2005/Atom".equals(namespace)) {
                return "atom";
            }
            return suggestion;
        });
    }

    public static Object createNamespacePrefixMapper(final BiFunction<String, String, String> prefix)
            throws JAXBException {
        Class<?> type;
        try {
            Class.forName("org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper");
            type = Class.forName("org.jboss.resteasy.plugins.providers.jaxb.hacks.NamespacePrefixMapper_3_0_Ri");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
                type = Class.forName("org.jboss.resteasy.plugins.providers.jaxb.hacks.NamespacePrefixMapper_2_1_Ri");
            } catch (ClassNotFoundException e2) {
                throw Messages.MESSAGES.namespacePrefixMapperNotInClassPath(e2);
            }
        }
        try {
            final Constructor<?> constructor = type.getDeclaredConstructor(BiFunction.class);
            return constructor.newInstance(prefix);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw Messages.MESSAGES.namespacePrefixMapperNotInClassPath(e);
        }
    }

    public static Marshaller createMarshaller(final JAXBContext context) throws JAXBException {
        return wrap(context.createMarshaller());
    }

    public static Unmarshaller createUnmarshaller(final JAXBContext context) throws JAXBException {
        return wrap(context.createUnmarshaller());
    }

    private static Marshaller wrap(final Marshaller marshaller) {
        if (marshaller instanceof DelegatingMarshaller) {
            return marshaller;
        }
        return new DelegatingMarshaller(marshaller);
    }

    private static Unmarshaller wrap(final Unmarshaller unmarshaller) {
        if (unmarshaller instanceof DelegatingUnmarshaller) {
            return unmarshaller;
        }
        return new DelegatingUnmarshaller(unmarshaller);
    }

}
