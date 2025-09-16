package org.jboss.resteasy.test.nextgen.wadl;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;

import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@RestBootstrap(WADLNettyContainerTest.TestApplication.class)
public class WADLNettyContainerTest {
    @Inject
    private static Client client;

    @Test
    public void test() throws Exception {
        TestWadlFunctions basicTest = new TestWadlFunctions();
        basicTest.setClient(client);
        basicTest.testBasicSet();
        basicTest.testResteasy1246();
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(BasicResource.class, RESTEASY1246.class, MyWadlResource.class);
        }
    }
}
