package org.jboss.resteasy.links.test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(value = SecureBookStore.class, configFactory = TestSecureLinks.TestConfigurationProvider.class)
public class TestSecureLinks extends AbstractTestSecureLinks {
}
