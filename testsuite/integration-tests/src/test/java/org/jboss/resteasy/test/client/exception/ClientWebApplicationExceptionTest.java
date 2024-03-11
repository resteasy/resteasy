package org.jboss.resteasy.test.client.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.RedirectionException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.exception.ResteasyBadRequestException;
import org.jboss.resteasy.client.exception.ResteasyClientErrorException;
import org.jboss.resteasy.client.exception.ResteasyForbiddenException;
import org.jboss.resteasy.client.exception.ResteasyInternalServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyNotAcceptableException;
import org.jboss.resteasy.client.exception.ResteasyNotAllowedException;
import org.jboss.resteasy.client.exception.ResteasyNotAuthorizedException;
import org.jboss.resteasy.client.exception.ResteasyNotFoundException;
import org.jboss.resteasy.client.exception.ResteasyNotSupportedException;
import org.jboss.resteasy.client.exception.ResteasyRedirectionException;
import org.jboss.resteasy.client.exception.ResteasyServerErrorException;
import org.jboss.resteasy.client.exception.ResteasyServiceUnavailableException;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionApplication;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionResource;
import org.jboss.resteasy.utils.PermissionUtil;
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
 * @tpChapter Client throws ResteasyWebApplicationException on server side
 * @tpSince RESTEasy 4.6.0.Final
 * @tpTestCaseDetails Test WebApplicationExceptions and WebApplicationExceptionWrappers in various circumstances
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientWebApplicationExceptionTest {

    public static Response commonResponse = Response.ok("msg").status(444).header("foo", "bar").build();

    private static ResponseBuilder commonBuilder = Response.ok("msg").header("foo", "bar");

    public static WebApplicationException[] oldExceptions = {

            // Redirection WebApplicationException
            new WebApplicationException(commonBuilder.status(333).build()),

            // The first four WebApplicationExceptions test the four WebApplicationException constructors
            // that take a Response parameter.
            new WebApplicationException(commonResponse),
            new WebApplicationException("msg", commonResponse),
            new WebApplicationException(new Exception(), commonResponse),
            new WebApplicationException("msg", new Exception(), commonResponse),

            // The other WebApplicationExceptions test the WebApplicationException subclasses that are
            // thrown according to the status used. The relationship between status and subclass is given
            // in oldExceptionMap.
            new WebApplicationException(commonBuilder.status(400).build()),
            new WebApplicationException(commonBuilder.status(401).build()),
            new WebApplicationException(commonBuilder.status(403).build()),
            new WebApplicationException(commonBuilder.status(404).build()),
            new WebApplicationException(Response.fromResponse(commonBuilder.status(405).build()).allow(HttpMethod.GET).build()),
            new WebApplicationException(commonBuilder.status(406).build()),
            new WebApplicationException(commonBuilder.status(415).build()),
            new WebApplicationException(commonBuilder.status(500).build()),
            new WebApplicationException(commonBuilder.status(503).build()),
            new WebApplicationException(commonBuilder.status(555).build()),
    };

    public static Map<Integer, Class<?>> oldExceptionMap = new HashMap<Integer, Class<?>>();
    static {
        oldExceptionMap.put(333, RedirectionException.class);
        oldExceptionMap.put(400, BadRequestException.class);
        oldExceptionMap.put(401, NotAuthorizedException.class);
        oldExceptionMap.put(403, ForbiddenException.class);
        oldExceptionMap.put(404, NotFoundException.class);
        oldExceptionMap.put(405, NotAllowedException.class);
        oldExceptionMap.put(406, NotAcceptableException.class);
        oldExceptionMap.put(415, NotSupportedException.class);
        oldExceptionMap.put(444, ClientErrorException.class);
        oldExceptionMap.put(500, InternalServerErrorException.class);
        oldExceptionMap.put(503, ServiceUnavailableException.class);
        oldExceptionMap.put(555, ServerErrorException.class);
    }

    public static WebApplicationException[] newExceptions = {
            // The first four ResteasyWebApplicationExceptions test the four ResteasyWebApplicationException
            // constructors that take a Response parameter.
            new ResteasyWebApplicationException(oldExceptions[0]),
            new ResteasyWebApplicationException(oldExceptions[1]),
            new ResteasyWebApplicationException(oldExceptions[2]),
            new ResteasyWebApplicationException(oldExceptions[3]),

            // The other ResteasyWebApplicationExceptions test the ResteasyWebApplicationExceptions subclasses
            // that are thrown according to the status used. The relationship between status and subclass is given
            // in newExceptionMap.
            new ResteasyWebApplicationException(oldExceptions[4]),
            new ResteasyWebApplicationException(oldExceptions[5]),
            new ResteasyWebApplicationException(oldExceptions[6]),
            new ResteasyWebApplicationException(oldExceptions[7]),
            new ResteasyWebApplicationException(oldExceptions[8]),
            new ResteasyWebApplicationException(oldExceptions[9]),
            new ResteasyWebApplicationException(oldExceptions[10]),
            new ResteasyWebApplicationException(oldExceptions[11]),
            new ResteasyWebApplicationException(oldExceptions[12]),
            new ResteasyWebApplicationException(oldExceptions[13]),
            new ResteasyWebApplicationException(oldExceptions[14]),
    };

    public static Map<Integer, Class<?>> newExceptionMap = new HashMap<Integer, Class<?>>();
    static {
        newExceptionMap.put(333, ResteasyRedirectionException.class);
        newExceptionMap.put(400, ResteasyBadRequestException.class);
        newExceptionMap.put(401, ResteasyNotAuthorizedException.class);
        newExceptionMap.put(403, ResteasyForbiddenException.class);
        newExceptionMap.put(404, ResteasyNotFoundException.class);
        newExceptionMap.put(405, ResteasyNotAllowedException.class);
        newExceptionMap.put(406, ResteasyNotAcceptableException.class);
        newExceptionMap.put(415, ResteasyNotSupportedException.class);
        newExceptionMap.put(444, ResteasyClientErrorException.class);
        newExceptionMap.put(500, ResteasyInternalServerErrorException.class);
        newExceptionMap.put(503, ResteasyServiceUnavailableException.class);
        newExceptionMap.put(555, ResteasyServerErrorException.class);
    }

    private static Client client;

    private static WebTarget oldBehaviorTarget;
    private static WebTarget newBehaviorTarget;

    public static final String oldBehaviorDeploymentName = "OldBehaviorClientWebApplicationExceptionTest";
    public static final String newBehaviorDeploymentName = "NewBehaviorClientWebApplicationExceptionTest";

    @BeforeAll
    public static void beforeClass() throws Exception {
        client = ClientBuilder.newClient();
        oldBehaviorTarget = client.target(PortProviderUtil.generateURL("/app/test/", oldBehaviorDeploymentName));
        newBehaviorTarget = client.target(PortProviderUtil.generateURL("/app/test/", newBehaviorDeploymentName));
    }

    @AfterAll
    public static void stop() throws Exception {
        client.close();
    }

    @Deployment(name = newBehaviorDeploymentName)
    public static Archive<?> deployNewBehavior() {
        WebArchive war = TestUtil.prepareArchive(newBehaviorDeploymentName)
                .addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                        new PropertyPermission("ipv6", "read"),
                        new RuntimePermission("getenv.RESTEASY_PORT"),
                        new PropertyPermission("org.jboss.resteasy.port", "read"),
                        new PropertyPermission("quarkus.tester", "read"),
                        new PropertyPermission("node", "read")), "permissions.xml");
        war.addClass(ClientWebApplicationExceptionTest.class);
        war.addClass(ClientWebApplicationExceptionApplication.class);
        war.addClass(ClientWebApplicationExceptionResource.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionResource.class);
    }

    @Deployment(name = oldBehaviorDeploymentName)
    public static Archive<?> deployOldBehaviour() {
        WebArchive war = TestUtil.prepareArchive(oldBehaviorDeploymentName)
                .addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                        new PropertyPermission("ipv6", "read"),
                        new RuntimePermission("getenv.RESTEASY_PORT"),
                        new PropertyPermission("org.jboss.resteasy.port", "read"),
                        new PropertyPermission("quarkus.tester", "read"),
                        new PropertyPermission("node", "read")), "permissions.xml");
        war.addClass(ClientWebApplicationExceptionTest.class);
        war.addClass(ClientWebApplicationExceptionApplication.class);
        war.addClass(ClientWebApplicationExceptionResource.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.setWebXML(ClientWebApplicationExceptionResteasyProxyTest.class.getPackage(), "webapplicationexception_web.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionResource.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @tpTestDetails For each WebApplicationException in oldExceptions, calls the resource method oldException() to throw
     *                that WebApplicationException. Since it is running on the client side, the standard behavior of throwing a
     *                WebApplicationException will occur. That WebApplicationException should match the WebApplicationException
     *                thrown by oldException().
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testOldExceptionsDirectly() {
        for (int i = 0; i < oldExceptions.length; i++) {
            try {
                newBehaviorTarget.path("exception/old/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException wae) {
                Response response = wae.getResponse();
                WebApplicationException oldException = oldExceptions[i];
                Assertions.assertEquals(oldException.getResponse().getStatus(), response.getStatus());
                Assertions.assertEquals(oldException.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
                Assertions.assertEquals(oldException.getResponse().getEntity(), response.readEntity(String.class));
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), wae.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails For each ResteasyWebApplicationException in newExceptions, calls the resource method newException() to
     *                throw
     *                that ResteasyWebApplicationException. Since it is running on the client side, the standard behavior of
     *                throwing a
     *                WebApplicationException will occur. That WebApplicationException should match the result returned by
     *                newException()
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNewExceptionsDirectly() {
        for (int i = 0; i < newExceptions.length; i++) {
            try {
                newBehaviorTarget.path("exception/new/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(newExceptions[i].getResponse().getStatus(), response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            }
        }
    }

    /**
     * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
     *                set to "true" to compel the original Client behavior on the server side.
     *
     *                2. For each WebApplicationException in oldExceptions, the resource method noCatchOld() is called.
     *
     *                3. noCatchOld() calls oldException(), which throws the chosen member of oldExceptions. The resulting
     *                HTTP response contains the status, headers, and entity in that WebApplicationException.
     *
     *                4. In noCatchOld(), the original behavior causes the HTTP response to be turned into a
     *                WebApplicationException,
     *                which is thrown by the Client. The resulting HTTP response contains the status, headers, and entity in
     *                that
     *                WebApplicationException.
     *
     *                5. The client side Client constructs and throws a WebApplicationException which is checked against the
     *                matching
     *                WebApplicationException in oldExceptins.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchOldBehaviorOldExceptions() {
        for (int i = 0; i < oldExceptions.length; i++) {
            try {
                oldBehaviorTarget.path("nocatch/old/old/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                WebApplicationException wae = oldExceptions[i];
                Assertions.assertEquals(wae.getResponse().getStatus(), response.getStatus());
                Assertions.assertEquals(wae.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
                Assertions.assertEquals(wae.getResponse().getEntity(), response.readEntity(String.class));
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
     *                set to "true" to compel the original Client behavior on the server side.
     *
     *                2. For each ResteasyWebApplicationException in newExceptions, the resource method noCatchNew() is called.
     *
     *                3. noCatchNew() calls newException(), which throws the matching member of newExceptions. The resulting
     *                Response is sanitized.
     *
     *                4. In noCatchNew(), the original behavior causes the HTTP response to be turned into a
     *                WebApplicationException,
     *                which is thrown by the Client. The resulting HTTP response is sanitized.
     *
     *                5. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchOldBehaviorNewExceptions() {
        for (int i = 0; i < newExceptions.length; i++) {
            try {
                oldBehaviorTarget.path("nocatch/old/new/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(newExceptions[i].getResponse().getStatus(), response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. For each WebApplicationException in oldExceptions, the resource method noCatchOld() is called.
     *
     *                2. noCatchOld() calls oldException(), which throws the matching member of oldExceptions. The resulting
     *                HTTP response contains the status, headers, and entity in that WebApplicationException.
     *
     *                3. In noCatchOld(), the new behavior causes the HTTP response to be turned into a
     *                WebApplicationExceptionWrapper,
     *                which is thrown by the Client. WebApplicationExceptionWrapper.getResponse() returns a sanitized Response.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchNewBehaviorOldExceptions() {
        for (int i = 0; i < oldExceptions.length; i++) {
            try {
                newBehaviorTarget.path("nocatch/new/old/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(oldExceptions[i].getResponse().getStatus(), response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. For each ResteasyWebApplicationException in newExceptions, the resource method noCatchNew() is called.
     *
     *                2. noCatchNew() calls newException(), which throws the matching member of newExceptions.
     *                WebApplicationExceptionWrapper.getResponse() returns a sanitized Response.
     *
     *                3. In noCatchNew(), the new behavior causes the HTTP response to be turned into a
     *                WebApplicationExceptionWrapper,
     *                which is thrown by the Client. The resulting HTTP response has a sanitized Response.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchNewBehaviorNewExceptions() {
        for (int i = 0; i < newExceptions.length; i++) {
            try {
                newBehaviorTarget.path("nocatch/new/new/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(newExceptions[i].getResponse().getStatus(), response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
     *                set to "true" to compel the original Client behavior on the server side.
     *
     *                2. For each WebApplicationException in oldExceptions, the resource method catchOldOld() is called.
     *
     *                3. catchOldOld() calls oldException(), which throws the chosen member of oldExceptions. The resulting
     *                HTTP response contains the status, headers, and entity in that WebApplicationException.
     *
     *                4. In catchOldOld(), the original behavior causes the HTTP response to be turned into a
     *                WebApplicationException,
     *                which is thrown by the Client. That WebApplicationException is caught, verified to match the matching
     *                WebApplicationException in oldExceptins, and then rethrown. The resulting HTTP response contains the
     *                status, headers, and entity in that WebApplicationException.
     *
     *                5. The client side Client constructs and throws a WebApplicationException which is checked against the
     *                matching
     *                WebApplicationException in oldExceptins.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testCatchOldBehaviorOldExceptions() {
        for (int i = 0; i < oldExceptions.length; i++) {
            try {
                oldBehaviorTarget.path("catch/old/old/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(oldExceptions[i].getResponse().getStatus(), response.getStatus());
                Assertions.assertEquals(oldExceptions[i].getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
                Assertions.assertEquals(oldExceptions[i].getResponse().getEntity(), response.readEntity(String.class));
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
     *                set to "true" to compel the original Client behavior on the server side.
     *
     *                2. For each ResteasyWebApplicationException in newExceptions, the resource method catchOldNew() is called.
     *
     *                3. catchOldNew() calls newException(), which throws the chosen member of newExceptions.
     *                WebApplicationExceptionWrapper.getResponse() returns a sanitized Response.
     *
     *                4. In catchOldNew(), the original behavior causes the HTTP response to be turned into a
     *                WebApplicationException,
     *                which is thrown by the Client. That WebApplicationException is caught, verified to
     *                have matching status, no added headers, and an empty entity, and then rethrown.
     *
     *                5. The client side Client constructs and throws a WebApplicationException which is verified to have
     *                matching status, no added headers, and an empty entity.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testCatchOldBehaviorNewExceptions() {
        for (int i = 0; i < newExceptions.length; i++) {
            try {
                oldBehaviorTarget.path("catch/old/new/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException e) {
                Assertions.fail("didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertNotNull(response);
                Assertions.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).length() == 0);
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. For each WebApplicationException in oldExceptions, the resource method catchNewOld() is called.
     *
     *                2. catchNewOld() calls oldException(), which throws the matching member of oldExceptions. The resulting
     *                HTTP response contains the status, headers, and entity in that WebApplicationException.
     *
     *                3. In catchNewOld(), the new behavior causes the HTTP response to be turned into a
     *                WebApplicationExceptionWrapper,
     *                which is thrown by the Client, caught, tested, and rethrown.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testCatchNewBehaviorOldExceptions() {
        for (int i = 0; i < oldExceptions.length; i++) {
            try {
                newBehaviorTarget.path("catch/new/old/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException e) {
                Assertions.fail("didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertNotNull(response);
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. For each ResteasyWebApplicationException in newExceptions, the resource method catchNewNew() is called.
     *
     *                2. catchNewNew() calls newException(), which throws the matching member of newExceptions. The resulting
     *                HTTP response will have a sanitized Response.
     *
     *                3. In catchNewNew(), the new behavior causes the HTTP response to be turned into a
     *                WebApplicationExceptionWrapper,
     *                which is thrown by the Client, caught, tested, and rethrown.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testCatchNewBehaviorNewExceptions() {
        for (int i = 0; i < newExceptions.length; i++) {
            try {
                newBehaviorTarget.path("catch/new/new/" + i).request().get(String.class);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException e) {
                Assertions.fail("didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertNotNull(response);
                Assertions.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                Assertions.assertEquals(oldExceptionMap.get(response.getStatus()), e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }
}
