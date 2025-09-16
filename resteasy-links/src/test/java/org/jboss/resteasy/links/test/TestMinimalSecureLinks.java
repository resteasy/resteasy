/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.links.test;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(value = TestMinimalSecureLinks.TestApplication.class, configFactory = TestMinimalSecureLinks.TestConfigurationProvider.class)
public class TestMinimalSecureLinks extends AbstractTestSecureLinks {

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(SecureBookStoreMinimal.class);
        }
    }
}
