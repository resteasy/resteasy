/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.links.test;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestMinimalLinks.TestApplication.class)
public class TestMinimalLinks extends AbstractTestLinks {

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ObjectMapperProvider.class, BookStoreMinimal.class);
        }
    }
}
