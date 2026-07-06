package org.jboss.resteasy.links.test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap({ ObjectMapperProvider.class, BookStore.class })
public class TestLinks extends AbstractTestLinks {
}
