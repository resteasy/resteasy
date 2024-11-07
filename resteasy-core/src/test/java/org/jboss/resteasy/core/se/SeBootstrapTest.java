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

package org.jboss.resteasy.core.se;

import java.util.concurrent.ExecutionException;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.Application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class SeBootstrapTest {

    @Test
    public void checkAnnotatedApplicationClass() throws Exception {
        final SeBootstrap.Configuration configuration = boot(AnnotatedApplication.class);
        Assertions.assertEquals("/annotated", configuration.rootPath());
    }

    @Test
    public void checkAnnotatedApplicationUriClass() throws Exception {
        final SeBootstrap.Configuration configuration = boot(AnnotatedApplication.class);
        Assertions.assertEquals("http://localhost:8081/annotated", configuration.baseUri().toString());
    }

    @Test
    public void checkNonAnnotatedApplicationClass() throws Exception {
        final SeBootstrap.Configuration configuration = boot(NonAnnotatedApplication.class);
        Assertions.assertEquals("/", configuration.rootPath());
    }

    @Test
    public void checkNonAnnotatedApplicationUriClass() throws Exception {
        final SeBootstrap.Configuration configuration = boot(NonAnnotatedApplication.class);
        Assertions.assertEquals("http://localhost:8081/", configuration.baseUri().toString());
    }

    @Test
    public void checkAnnotatedApplicationClassOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(AnnotatedApplication.class, SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("override", configuration.rootPath());
    }

    @Test
    public void checkAnnotatedApplicationUriClassOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(AnnotatedApplication.class, SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("http://localhost:8081/override", configuration.baseUri().toString());
    }

    @Test
    public void checkNonAnnotatedApplicationClassOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(NonAnnotatedApplication.class, SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("override", configuration.rootPath());
    }

    @Test
    public void checkNonAnnotatedApplicationUriClassOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(NonAnnotatedApplication.class, SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("http://localhost:8081/override", configuration.baseUri().toString());
    }

    @Test
    public void checkAnnotatedApplicationInstance() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new AnnotatedApplication());
        Assertions.assertEquals("/annotated", configuration.rootPath());
    }

    @Test
    public void checkAnnotatedApplicationUriInstance() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new AnnotatedApplication());
        Assertions.assertEquals("http://localhost:8081/annotated", configuration.baseUri().toString());
    }

    @Test
    public void checkNonAnnotatedApplicationInstance() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new NonAnnotatedApplication());
        Assertions.assertEquals("/", configuration.rootPath());
    }

    @Test
    public void checkNonAnnotatedApplicationUriInstance() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new NonAnnotatedApplication());
        Assertions.assertEquals("http://localhost:8081/", configuration.baseUri().toString());
    }

    @Test
    public void checkAnnotatedApplicationInstanceOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new AnnotatedApplication(), SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("override", configuration.rootPath());
    }

    @Test
    public void checkAnnotatedApplicationUriInstanceOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new AnnotatedApplication(), SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("http://localhost:8081/override", configuration.baseUri().toString());
    }

    @Test
    public void checkNonAnnotatedApplicationInstanceOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new NonAnnotatedApplication(), SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("override", configuration.rootPath());
    }

    @Test
    public void checkNonAnnotatedApplicationUriInstanceOverridden() throws Exception {
        final SeBootstrap.Configuration configuration = boot(new NonAnnotatedApplication(), SeBootstrap.Configuration.builder()
                .rootPath("override").build());
        Assertions.assertEquals("http://localhost:8081/override", configuration.baseUri().toString());
    }

    private SeBootstrap.Configuration boot(final Class<? extends Application> clazz)
            throws ExecutionException, InterruptedException {
        final SeBootstrap.Instance instance = SeBootstrap.start(clazz)
                .toCompletableFuture().get();
        return instance.configuration();
    }

    private SeBootstrap.Configuration boot(final Class<? extends Application> clazz,
            final SeBootstrap.Configuration configuration)
            throws ExecutionException, InterruptedException {
        final SeBootstrap.Instance instance = SeBootstrap.start(clazz, configuration)
                .toCompletableFuture().get();
        return instance.configuration();
    }

    private SeBootstrap.Configuration boot(final Application application) throws ExecutionException, InterruptedException {
        final SeBootstrap.Instance instance = SeBootstrap.start(application)
                .toCompletableFuture().get();
        return instance.configuration();
    }

    private SeBootstrap.Configuration boot(final Application application, SeBootstrap.Configuration configuration)
            throws ExecutionException, InterruptedException {
        final SeBootstrap.Instance instance = SeBootstrap.start(application, configuration)
                .toCompletableFuture().get();
        return instance.configuration();
    }

    @ApplicationPath("/annotated")
    public static class AnnotatedApplication extends Application {

    }

    public static class NonAnnotatedApplication extends Application {

    }
}
