package org.jboss.resteasy.test.client.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionProxyResourceInterface;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionResteasyProxyApplication;
import org.jboss.resteasy.test.client.exception.resource.ClientWebApplicationExceptionResteasyProxyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client throws ResteasyWebApplicationException on server side
 * @tpSince RESTEasy 4.6.0.Final
 * @tpTestCaseDetails Test WebApplicationExceptions and WebApplicationExceptionWrapperss in various circumstances,
 *                    calls made by Resteasy client proxies.
 *
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(LoggingSetupTask.class)
public class ClientWebApplicationExceptionResteasyProxyTest {

    public static final String oldBehaviorDeploymentName = "OldBehaviorClientWebApplicationExceptionResteasyProxyTest";
    public static final String newBehaviorDeploymentName = "NewBehaviorClientWebApplicationExceptionResteasyProxyTest";

    private static ClientWebApplicationExceptionProxyResourceInterface oldBehaviorProxy;
    private static ClientWebApplicationExceptionProxyResourceInterface newBehaviorProxy;

    static {
        ResteasyClient client = (ResteasyClient) ResteasyClientBuilder.newClient();
        oldBehaviorProxy = client.target(PortProviderUtil.generateURL("/app/test/", oldBehaviorDeploymentName))
                .proxy(ClientWebApplicationExceptionProxyResourceInterface.class);
        newBehaviorProxy = client.target(PortProviderUtil.generateURL("/app/test/", newBehaviorDeploymentName))
                .proxy(ClientWebApplicationExceptionProxyResourceInterface.class);
    }

    @Deployment(name = oldBehaviorDeploymentName)
    public static Archive<?> deployOldBehaviour() {
        WebArchive war = TestUtil.prepareArchive(oldBehaviorDeploymentName);
        war.addClass(ClientWebApplicationExceptionTest.class);
        war.addClass(ClientWebApplicationExceptionResteasyProxyApplication.class);
        war.addClass(ClientWebApplicationExceptionResteasyProxyResource.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.setWebXML(ClientWebApplicationExceptionResteasyProxyTest.class.getPackage(), "webapplicationexception_web.xml");
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionProxyResourceInterface.class);
    }

    @Deployment(name = newBehaviorDeploymentName)
    public static Archive<?> deployNewBehavior() {
        WebArchive war = TestUtil.prepareArchive(newBehaviorDeploymentName);
        war.addClass(ClientWebApplicationExceptionTest.class);
        war.addClass(ClientWebApplicationExceptionResteasyProxyApplication.class);
        war.addClass(ClientWebApplicationExceptionResteasyProxyResource.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(TestUtil.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientWebApplicationExceptionProxyResourceInterface.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @tpTestDetails For each WebApplicationException in oldExceptions, calls the resource method oldException() to throw
     *                that WebApplicationException. Since it is running on the client side, the standard behavior of throwing a
     *                WebApplicationException will occur. That WebApplicationException should match the WebApplicationException
     *                thrown by oldException().
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testOldExceptionsDirectly() {
        for (int i = 0; i < ClientWebApplicationExceptionTest.oldExceptions.length; i++) {
            try {
                System.setProperty("node", "localhost");
                newBehaviorProxy.oldException(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException wae) {
                Response response = wae.getResponse();
                WebApplicationException oldException = ClientWebApplicationExceptionTest.oldExceptions[i];
                Assertions.assertEquals(oldException.getResponse().getStatus(), response.getStatus());
                Assertions.assertEquals(oldException.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
                Assertions.assertEquals(oldException.getResponse().getEntity(), response.readEntity(String.class));
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        wae.getClass());
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
     *                newException().
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNewExceptionsDirectly() throws Exception {
        for (int i = 0; i < ClientWebApplicationExceptionTest.newExceptions.length; i++) {
            try {
                newBehaviorProxy.newException(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.oldExceptions.length; i++) {
            try {
                oldBehaviorProxy.noCatchOldOld(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                WebApplicationException wae = ClientWebApplicationExceptionTest.oldExceptions[i];
                Assertions.assertEquals(wae.getResponse().getStatus(), response.getStatus());
                Assertions.assertEquals(wae.getResponse().getHeaderString("foo"), response.getHeaderString("foo"));
                Assertions.assertEquals(wae.getResponse().getEntity(), response.readEntity(String.class));
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. The value of ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR is
     *                set to "true" to compel the original Client behavior on the server side.
     *
     *                2. For each ResteasyWebApplicationException in ClientWebApplicationExceptionTest.newExceptions,
     *                the resource method noCatchNew() is called.
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.newExceptions.length; i++) {
            try {
                oldBehaviorProxy.noCatchOldNew(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
     *                ResteasyWebApplicationException,
     *                which is thrown by the Client. ResteasyWebApplicationException.getResponse() returns a sanitized Response.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchNewBehaviorOldExceptions() {
        for (int i = 0; i < ClientWebApplicationExceptionTest.oldExceptions.length; i++) {
            try {
                newBehaviorProxy.noCatchNewOld(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }

    /**
     * @tpTestDetails 1. For each ResteasyWebApplicationException in newExceptions, the resource method noCatchNew() is called.
     *
     *                2. noCatchNew() calls newException(), which throws the matching member of newExceptions.
     *                ResteasyWebApplicationException.getResponse() returns a sanitized Response.
     *
     *                3. In noCatchNew(), the new behavior causes the HTTP response to be turned into a
     *                ResteasyWebApplicationException,
     *                which is thrown by the Client. The resulting HTTP response has a sanitized Response.
     *
     *                4. The client side Client constructs and throws a WebApplicationException which is checked for a sanitized
     *                Response and matching status.
     *
     * @tpSince RESTEasy 4.6.0.Final
     */
    @Test
    public void testNoCatchNewBehaviorNewExceptions() {
        for (int i = 0; i < ClientWebApplicationExceptionTest.newExceptions.length; i++) {
            try {
                newBehaviorProxy.noCatchNewNew(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(ClientWebApplicationExceptionTest.newExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertNull(response.getHeaderString("foo"));
                Assertions.assertTrue(response.readEntity(String.class).isEmpty());
                // We compare the old exception here because this is coming from a client resulting in the exception thrown
                // at the client not wrapped.
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.oldExceptions.length; i++) {
            try {
                oldBehaviorProxy.catchOldOld(i);
                Assertions.fail("expected exception");
            } catch (ResteasyWebApplicationException rwae) {
                Assertions.fail("Didn't expect ResteasyWebApplicationException");
            } catch (WebApplicationException e) {
                Response response = e.getResponse();
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getStatus(),
                        response.getStatus());
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getHeaderString("foo"),
                        response.getHeaderString("foo"));
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptions[i].getResponse().getEntity(),
                        response.readEntity(String.class));
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
     *                ResteasyWebApplicationException.getResponse() returns a sanitized Response.
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.newExceptions.length; i++) {
            try {
                oldBehaviorProxy.catchOldNew(i);
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
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.oldExceptions.length; i++) {
            try {
                newBehaviorProxy.catchNewOld(i);
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
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
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
        for (int i = 0; i < ClientWebApplicationExceptionTest.newExceptions.length; i++) {
            try {
                newBehaviorProxy.catchNewNew(i);
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
                Assertions.assertEquals(ClientWebApplicationExceptionTest.oldExceptionMap.get(response.getStatus()),
                        e.getClass());
            } catch (Exception e) {
                Assertions.fail("expected WebApplicationException");
            }
        }
    }
}
