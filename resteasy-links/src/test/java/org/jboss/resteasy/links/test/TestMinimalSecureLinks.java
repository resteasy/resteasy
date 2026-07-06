/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.links.test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(value = SecureBookStoreMinimal.class, configFactory = TestMinimalSecureLinks.TestConfigurationProvider.class)
public class TestMinimalSecureLinks extends AbstractTestSecureLinks {
}
