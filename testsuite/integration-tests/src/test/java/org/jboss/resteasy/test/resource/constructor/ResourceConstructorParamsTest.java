package org.jboss.resteasy.test.resource.constructor;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.test.resource.constructor.resource.ConstructorNoParamsResource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParams400Resource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParams404Resource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParamsMixedResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * The JAX-RS spec defines reporting rules when instantiating resource class
 * constructor methods.  Section 3 of the spec provides the details.
 *
 * A WebApplicationException thrown during construction is processed directly
 * as described in section 3.3.4.
 *
 * All other exceptions are treated as client  errors.
 *
 *    If the resource's construcutor input parameter is annotated with @MatrixParam ,
 *    @QueryParam or @PathParam, an implementation must throw an instance of
 *    NotFoundException (404 status) that wraps the thrown exception and no entity;
 *
 *    If the resource's construcutor input parameter is annotated with @HeaderParam
 *    or @CookieParam, an implementation must throw an instance of BadRequestException
 *    (400 status) that wraps the thrown exception and no entity.
 *
 *    There is no rule defining the exception to be thrown when the constructors
 *    input parameter list has a combination of the 2 annotations groups above.
 *    Resteasy throws a BadRequestException (400 status) in this case.
 *
 *    When there are no input parameters in the constructor and it throws an
 *    exception other than WebApplicationException, this implementation throws
 *    an (500 status) ApplicationException.
 */

@RunWith(Arquillian.class)
@RunAsClient
public class ResourceConstructorParamsTest {
    protected static final Logger logger = Logger.getLogger(
            ResourceConstructorParamsTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(
                ResourceConstructorParamsTest.class.getSimpleName());
        war.addClass(ConstructorParams400Resource.class);
        war.addClass(ConstructorParams404Resource.class);
        war.addClass(ConstructorParamsMixedResource.class);
        war.addClass(ConstructorNoParamsResource.class);
        return TestUtil.finishContainerPrepare(war, null);
    }

    @Before
    public void init() {
        client = (ResteasyClient)ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path,
                ResourceConstructorParamsTest.class.getSimpleName());
    }

    @Test
    public void param400Test() {
        Response response = client.target(generateURL("/params400/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 400,
                response.getStatus());
        response.bufferEntity();
        String msg = response.readEntity(String.class);
        Assert.assertTrue(msg.startsWith("RESTEASY003325:"));
    }
    @Test
    public void param404Test() {
        Response response = client.target(generateURL("/params404/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 404,
                response.getStatus());
        response.bufferEntity();
        String msg = response.readEntity(String.class);
        Assert.assertTrue(msg.startsWith("RESTEASY003325:"));
    }
    @Test
    public void mixedTest() {
        Response response = client.target(generateURL("/mixed/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 400,
                response.getStatus());
        response.bufferEntity();
        String msg = response.readEntity(String.class);
        Assert.assertTrue(msg.startsWith("RESTEASY003325:"));
    }
    @Test
    public void noParamsTest() {
        Response response = client.target(generateURL("/noparams/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 500,
                response.getStatus());
    }
}
