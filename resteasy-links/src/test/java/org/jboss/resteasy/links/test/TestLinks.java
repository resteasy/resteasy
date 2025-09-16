package org.jboss.resteasy.links.test;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestLinks.TestApplication.class)
public class TestLinks extends AbstractTestLinks {

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ObjectMapperProvider.class, BookStore.class);
        }
    }
}
