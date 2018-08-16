package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonArrayConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonListConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonSetConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonSortedSetConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonWithConverter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 *                    org.jboss.resteasy.test.resource.param.resource.MultiValuedParamPersonWithConverter class is used
 * @tpSince RESTEasy 3.6.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamCustomClassTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamCustomClassTest.class.getSimpleName());
        war.addClass(MultiValuedParamPersonWithConverter.class);
        war.addClass(MultiValuedParamPersonListConverter.class);
        war.addClass(MultiValuedParamPersonSetConverter.class);
        war.addClass(MultiValuedParamPersonSortedSetConverter.class);
        war.addClass(MultiValuedParamPersonArrayConverter.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedParamPersonConverterProvider.class,
                MultiValuedParamPersonResource.class,  MultiValuedParamPersonResource.QueryParamResource.class,
                MultiValuedParamPersonResource.HeaderParamResource.class, MultiValuedParamPersonResource.MatrixParamResource.class,
                MultiValuedParamPersonResource.CookieParamResource.class, MultiValuedParamPersonResource.PathParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamCustomClassTest.class.getSimpleName());
    }


    /**
     * Define testcase data set
     */
    static String name1 = "George";
    static String name2 = "Jack";
    static String name3 = "John";
    static MultiValuedParamPersonWithConverter person1 = new MultiValuedParamPersonWithConverter();
    static MultiValuedParamPersonWithConverter person2 = new MultiValuedParamPersonWithConverter();
    static MultiValuedParamPersonWithConverter person3 = new MultiValuedParamPersonWithConverter();
    static String expectedResponse;
    static {
        person1.setName(name1);
        person2.setName(name2);
        person3.setName(name3);
        expectedResponse = person1 + "," + person2 + "," + person3;
    }


    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testQueryParam() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_list")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_arrayList")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_set")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_hashSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_sortedSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversion_treeSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/queryParam/customConversion_array")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }


    /**
     * @tpTestDetails HeaderParam test
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testHeaderParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;
            response = client.target(generateBaseUrl() + "/headerParam/customConversion_list")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/headerParam/customConversion_arrayList")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/headerParam/customConversion_set")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/headerParam/customConversion_hashSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/headerParam/customConversion_sortedSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/headerParam/customConversion_treeSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/headerParam/customConversion_array")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails MatrixParam test
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testMatrixParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;
            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_list")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_arrayList")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_set")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_hashSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_sortedSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_treeSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/matrixParam/customConversion_array")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails CookieParam test
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testCookieParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;

            // cookies doesn't allow to use ',', see the spec (https://tools.ietf.org/html/rfc6265), so we need to use '-'
            String requestString = name1 + "-" + name2 + "-" + name3;

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_list")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_arrayList")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_set")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_hashSet")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_sortedSet")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_treeSet")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/cookieParam/customConversion_array")
                    .request().cookie("person", requestString).get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails PathParam test
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testPathParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;
            response = client.target(generateBaseUrl() + "/pathParam/customConversion_list/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/pathParam/customConversion_arrayList/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/pathParam/customConversion_set/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/pathParam/customConversion_hashSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/pathParam/customConversion_sortedSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/pathParam/customConversion_treeSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/pathParam/customConversion_array/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }

}
