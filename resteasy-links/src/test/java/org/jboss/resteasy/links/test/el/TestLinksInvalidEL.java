package org.jboss.resteasy.links.test.el;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Application;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.links.test.BookStoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;

@RestBootstrap(TestLinksInvalidEL.TestApplication.class)
public class TestLinksInvalidEL {

    private static final Logger LOG = Logger.getLogger(TestLinksInvalidEL.class);

    private BookStoreService client;

    @BeforeEach
    public void before(ResteasyWebTarget webTarget) {
        client = webTarget.proxy(BookStoreService.class);
    }

    @Test
    public void testELWorksWithoutPackageXML() throws Exception {
        try {
            client.getBookXML("foo");
            Assertions.fail("This should have caused a 500");
        } catch (InternalServerErrorException x) {
            LOG.error("Failure is " + x.getResponse().readEntity(String.class));
            Assertions.assertEquals(500, x.getResponse().getStatus());
        } catch (Exception x) {
            Assertions.fail("Expected InternalServerErrorException");
        }
    }

    @Test
    public void testELWorksWithoutPackageJSON() throws Exception {
        try {
            client.getBookJSON("foo");
            Assertions.fail("This should have caused a 500");
        } catch (InternalServerErrorException x) {
            LOG.error("Failure is " + x.getResponse().readEntity(String.class));
            Assertions.assertEquals(500, x.getResponse().getStatus());
        } catch (Exception x) {
            Assertions.fail("Expected InternalServerErrorException");
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(BookStoreInvalidEL.class);
        }
    }
}
