package org.jboss.resteasy.test.exception;

import java.lang.reflect.Type;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperAbstractExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperMyCustomException;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperMyCustomExceptionMapper;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperMyCustomSubException;
import org.jboss.resteasy.test.exception.resource.ExceptionMapperResource;
import org.jboss.resteasy.test.exception.resource.NotFoundExceptionMapper;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExceptionMapperTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ExceptionMapperTest.class.getSimpleName());
        war.addClasses(ExceptionMapperAbstractExceptionMapper.class);
        return TestUtil.finishContainerPrepare(war, null, ExceptionMapperResource.class,
                ExceptionMapperMyCustomExceptionMapper.class, ExceptionMapperMyCustomException.class,
                ExceptionMapperMyCustomSubException.class, NotFoundExceptionMapper.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ExceptionMapperTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request to the server, which causes application custom exception being thrown, this
     *                exception is caught by application provided ExceptionMapper. The application provides two providers for
     *                mapping
     *                exceptions. General RuntimeException mapping and MyCustomException mapping which extends the
     *                RuntimeException.
     * @tpPassCrit The more specific MyCustomException mapping will be used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomExceptionsUsed() {
        Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ExceptionMapperMyCustomExceptionMapper.class,
                ExceptionMapper.class)[0];
        Assertions.assertEquals(ExceptionMapperMyCustomException.class, exceptionType);
        Response response = client.target(generateURL("/resource/custom")).request().get();
        Assertions.assertNotEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus(),
                "General RuntimeException mapper was used instead of more specific one");
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(response.readEntity(String.class), "custom");
    }

    /**
     * @tpTestDetails Client sends GET request to the server, which causes WebapplicationException being thrown, this
     *                exception is caught by application provided ExceptionMapper
     * @tpPassCrit Application provided ExceptionMapper serves the exception and creates response with ACCEPTED status
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWAEResponseUsed() {
        Response response = client.target(generateURL("/resource/responseok")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(response.readEntity(String.class), "hello");
    }

    /**
     * @tpTestDetails Client sends GET request to the server, which causes the subclass of an exception
     *                which has an ExceptionMapper to be thrown. This subclass exception is caught by application provided
     *                ExceptionMapper
     * @tpPassCrit Application provided ExceptionMapper serves the exception and creates response with ACCEPTED status
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testCustomSubExceptionsUsed() {
        Response response = client.target(generateURL("/resource/sub")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(response.readEntity(String.class), "custom");
    }

    /**
     * @tpTestDetails Client sends GET request to a nonexistent resource, which causes a NotFoundException to be thrown.
     *                The NotFoundException is caught by the application provided NotFoundExceptionMapper, which sends a 410
     *                status.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testNotFoundExceptionMapping() {
        Response response = client.target(generateURL("/bogus")).request().get();
        Assertions.assertEquals(410, response.getStatus());
        response.close();
    }
}
