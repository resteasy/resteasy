/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class FakeHttpServerExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, ParameterResolver {
    private final Lock lock = new ReentrantLock();
    private final List<FakeHttpServer> servers = new ArrayList<>();

    @Override
    public void beforeAll(final ExtensionContext context) {
        injectFields(context, null, createServer(), ReflectionUtils::isStatic);
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        final FakeHttpServer httpServer = createServer();
        context.getRequiredTestInstances().getAllInstances() //
                .forEach(instance -> injectFields(context, instance, httpServer, ReflectionUtils::isNotFinal));
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        try {
            lock.lock();
            servers.forEach(FakeHttpServer::stop);
            servers.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.isAnnotated(TestServer.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return createServer();
    }

    private void injectFields(final ExtensionContext context, final Object instance, final FakeHttpServer testServer,
            final Predicate<Field> filter) {
        AnnotationUtils.findAnnotatedFields(context.getRequiredTestClass(), TestServer.class, filter)
                .forEach(field -> {
                    if (ReflectionUtils.isFinal(field)) {
                        throw new ExtensionConfigurationException(
                                String.format("@TestServer field %s.%s cannot be declared final.",
                                        context.getTestClass().map(Class::getName).orElse(""), field));
                    }
                    try {
                        ReflectionUtils.makeAccessible(field)
                                .set(instance, testServer);
                    } catch (IllegalAccessException e) {
                        throw new ExtensionConfigurationException(String.format("Failed to set the field %s.%s.",
                                context.getTestClass().map(Class::getName).orElse(""), field), e);
                    }
                });
    }

    private FakeHttpServer createServer() {
        final FakeHttpServer result = new FakeHttpServer();
        try {
            lock.lock();
            servers.add(result);
            return result;
        } finally {
            lock.unlock();
        }
    }
}
