package org.jboss.resteasy.test.nextgen.wadl;

import jakarta.ws.rs.client.Client;

import org.jboss.resteasy.test.nextgen.wadl.resources.BasicResource;
import org.jboss.resteasy.test.nextgen.wadl.resources.issues.RESTEASY1246;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@RestBootstrap({ BasicResource.class, RESTEASY1246.class, MyWadlResource.class })
public class WADLContainerTest {
    @RestResource
    private static Client client;

    @Test
    public void test() throws Exception {
        TestWadlFunctions basicTest = new TestWadlFunctions();
        basicTest.setClient(client);
        basicTest.testBasicSet();
        basicTest.testResteasy1246();
    }
}
