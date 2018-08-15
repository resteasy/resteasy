package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.path.resource.EmailResource;
import org.jboss.resteasy.test.resource.path.resource.PathParamCarResource;
import org.jboss.resteasy.test.resource.path.resource.PathParamDigits;
import org.jboss.resteasy.test.resource.path.resource.PathParamResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Spec requires that HEAD and OPTIONS are handled in a default manner
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class PathParamTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathLimitedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, PathParamDigits.class, PathParamResource.class,
                            PathParamCarResource.class, EmailResource.class);
    }

    /**
     * @tpTestDetails Check 6 parameters on path.
     *                Client invokes GET on root resource at /PathParamTest;
     *                Verify that right Method is invoked using
     *                PathParam primitive type List<String>.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test6() throws Exception {

        String[] Headers = {"list=abcdef"};

        ResteasyClient client = new ResteasyClientBuilder().build();
        for (String header : Headers) {
            Invocation.Builder request = client.target(PortProviderUtil.generateURL("/PathParamTest/a/b/c/d/e/f", PathLimitedTest.class.getSimpleName())).request();
            request.header("Accept", "text/plain");
            Response response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(header, response.readEntity(String.class));
        }
        client.close();
    }

    /**
     * @tpTestDetails Check digits on path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test178() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        {
            Invocation.Builder request = client.target(PortProviderUtil.generateURL("/digits/5150", PathLimitedTest.class.getSimpleName())).request();
            Response response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            response.close();
        }

        {
            Invocation.Builder request = client.target(PortProviderUtil.generateURL("/digits/5150A", PathLimitedTest.class.getSimpleName())).request();
            Response response = request.get();
            Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
            response.close();
        }
        client.close();
    }

    /**
     * @tpTestDetails Check example car resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCarResource() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(PortProviderUtil.generateURL("/cars/mercedes/matrixparam/e55;color=black/2006", PathLimitedTest.class.getSimpleName())).request();
        Response response = request.get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("A black 2006 mercedes e55", response.readEntity(String.class));
        // This must be a typo.  Should be "A midnight blue 2006 Porsche 911 Carrera S".

        request = client.target(PortProviderUtil.generateURL("/cars/mercedes/pathsegment/e55;color=black/2006", PathLimitedTest.class.getSimpleName())).request();
        response = request.get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("A black 2006 mercedes e55", response.readEntity(String.class));

        request = client.target(PortProviderUtil.generateURL("/cars/mercedes/pathsegments/e55/amg/year/2006", PathLimitedTest.class.getSimpleName())).request();
        response = request.get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("A 2006 mercedes e55 amg", response.readEntity(String.class));

        request = client.target(PortProviderUtil.generateURL("/cars/mercedes/uriinfo/e55;color=black/2006", PathLimitedTest.class.getSimpleName())).request();
        response = request.get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("A black 2006 mercedes e55", response.readEntity(String.class));
        client.close();
    }

    /**
     * @tpTestDetails Test email format on path
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testEmail() throws Exception {
       ResteasyClient client = new ResteasyClientBuilder().build();
       Response response = client.target(PortProviderUtil.generateURL("/employeeinfo/employees/bill.burke@burkecentral.com", PathLimitedTest.class.getSimpleName())).request().get();
       String str = response.readEntity(String.class);
       Assert.assertEquals("burke", str);
    }
}
