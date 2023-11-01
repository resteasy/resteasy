package org.jboss.resteasy.test.resteasy1056;

import java.net.URI;

import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.resteasy1056.TestApplication;
import org.jboss.resteasy.resteasy1056.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * RESTEASY-1056
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *          <p>
 *          Copyright June 7, 2014
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MissingCDITest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1056.war")
                .addClasses(TestApplication.class, TestResource.class)
                .addAsWebInfResource("web.xml");
        return war;
    }

    @ArquillianResource
    URI baseUri;

    @Test
    public void testMissingCDIValid() throws Exception {
        Response response = ResteasyClientBuilder.newClient().target(baseUri.toString() + "test/17").request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("17", entity);
    }

    @Test
    public void testMissingCDIInvalid() throws Exception {
        Response response = ResteasyClientBuilder.newClient().target(baseUri.toString() + "test/0").request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(400, response.getStatus());
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
        countViolations(e, 1, 0, 0, 1, 0);
        ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"));
    }

    protected void countViolations(ResteasyViolationException e, int totalCount, int propertyCount, int classCount,
            int parameterCount, int returnValueCount) {
        Assertions.assertEquals(totalCount, e.getViolations().size());
        Assertions.assertEquals(propertyCount, e.getPropertyViolations().size());
        Assertions.assertEquals(classCount, e.getClassViolations().size());
        Assertions.assertEquals(parameterCount, e.getParameterViolations().size());
        Assertions.assertEquals(returnValueCount, e.getReturnValueViolations().size());
    }
}
