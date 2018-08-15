package org.jboss.resteasy.test.resource.param;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamCdiResource;
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
 * @tpTestCaseDetails Test of CDI integration for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 * @tpSince RESTEasy 3.6.1
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamCdiTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamCdiTest.class.getSimpleName());
        war.addClass(MultiValuedParamPersonWithConverter.class);
        war.addClass(MultiValuedParamPersonListConverter.class);
        war.addClass(MultiValuedParamPersonSetConverter.class);
        war.addClass(MultiValuedParamPersonSortedSetConverter.class);
        war.addClass(MultiValuedParamPersonArrayConverter.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedParamPersonConverterProvider.class,
                MultiValuedParamCdiResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamCdiTest.class.getSimpleName());
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
     * @tpTestDetails Check queryParam in CDI integration
     * @tpSince RESTEasy 3.6.1
     */
    @Test
    public void testQueryParam() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            Response response;

            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_list")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_arrayList")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_set")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_hashSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_sortedSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_treeSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();


            response = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_array")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response.readEntity(String.class));
            response.close();

        } finally {
            client.close();
        }
    }
}
