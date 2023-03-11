package org.jboss.resteasy.test.resource.constructor;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorCookieParamWAEResource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorNoParamsResource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParams400Resource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParams404Resource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorParamsMixedResource;
import org.jboss.resteasy.test.resource.constructor.resource.ConstructorQueryParamWAEResource;
import org.jboss.resteasy.test.resource.constructor.resource.Item;
import org.jboss.resteasy.test.resource.constructor.resource.Item2;
import org.jboss.resteasy.test.resource.constructor.resource.Item2ParamConverterProvider;
import org.jboss.resteasy.test.resource.constructor.resource.ItemParamConverterProvider;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        war.addClass(ConstructorParamsMixedResource.class);
        war.addClass(ConstructorNoParamsResource.class);
        war.addClass(ItemParamConverterProvider.class);
        war.addClass(Item.class);
        war.addClass(Item2ParamConverterProvider.class);
        war.addClass(Item2.class);
        war.addClass(ConstructorParams404Resource.class);
        war.addClass(ConstructorParams400Resource.class);
        war.addClass(ConstructorCookieParamWAEResource.class);
        war.addClass(ConstructorQueryParamWAEResource.class);
        return TestUtil.finishContainerPrepare(war, null);
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
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
    public void mixedTest() {
        Response response = client.target(generateURL("/mixed/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 500,
                response.getStatus());
    }

    @Test
    public void noParamsTest() {
        Response response = client.target(generateURL("/noparams/get"))
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 500,
                response.getStatus());
    }

    @Test
    public void params404Test() {
        Response response = client.target(generateURL("/params404/get"))
                .queryParam("queryP", "A")
                .request()
                .get();
        Assert.assertEquals("Incorrect status code", 404,
                response.getStatus());
    }

    @Test
    public void params400Test() {
        Response response = client.target(generateURL("/params400/get"))
                .request()
                .cookie("cookieP", "A")
                .get();
        Assert.assertEquals("Incorrect status code", 400,
                response.getStatus());
    }

    @Test
    public void paramsCookieWebApplicationExceptionTest() {
        Response response = client.target(generateURL("/paramsWAECookie/get"))
                .request()
                .cookie("cookieP", "A")
                .get();

        // 405 is just a random one to verify WebApplicationException is not wrapped with NotFoundException.
        Assert.assertEquals("Incorrect status code", 405,
                response.getStatus());
    }

    @Test
    public void paramsQueryWebApplicationExceptionTest() {
        Response response = client.target(generateURL("/paramsWAEQuery/get"))
                .queryParam("queryP", "A")
                .request()
                .get();

        // 405 is just a random one to verify WebApplicationException is not wrapped with NotFoundException.
        Assert.assertEquals("Incorrect status code", 405,
                response.getStatus());
    }
}
