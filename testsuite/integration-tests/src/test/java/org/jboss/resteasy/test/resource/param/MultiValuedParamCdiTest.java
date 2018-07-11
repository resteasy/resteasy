package org.jboss.resteasy.test.resource.param;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.resource.param.resource.*;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param
 * @tpSince RESTEasy 3.5.1.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
@ApplicationScoped
public class MultiValuedParamCdiTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamCdiTest.class.getSimpleName());
        war.addClass(MultiValuedParam.class);
        war.addClass(ParamWrapper.class);
        war.addClass(MultiValuedCookieParam.class);
        war.addClass(CookieParamWrapper.class);
        war.addClass(MultiValuedPathParam.class);
        war.addClass(PathParamWrapper.class);
        war.addClass(PersonWithConstructor.class);
        war.addClass(PersonWithValueOf.class);
        war.addClass(PersonWithFromString.class);
        war.addClass(PersonWithConverter.class);
        war.addClass(PersonParamListConverter.class);
        war.addClass(PersonParamSetConverter.class);
        war.addClass(PersonParamSortedSetConverter.class);
        war.addClass(PersonParamArrayConverter.class);
        war.addClass(DateParamConverter.class);
        war.addClass(MultiValuedParamConverter.class);
        war.addClass(ParamWrapperArrayConverter.class);
        war.addClass(MultiValuedCookieParamConverter.class);
        war.addClass(CookieParamWrapperArrayConverter.class);
        war.addClass(MultiValuedPathParamConverter.class);
        war.addClass(PathParamWrapperArrayConverter.class);
        war.addClass(MultiValuedParamResourceClient.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedPersonParamConverterProvider.class,
                MultiValuedParamCdiResource.class,  MultiValuedParamCdiResource.QueryParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamCdiTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5.1
     */
    @Test
    public void testQueryParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            String name1 = "George";
            String name2 = "Jack";
            String name3 = "John";
            PersonWithConverter person1 = new PersonWithConverter();
            person1.setName(name1);
            PersonWithConverter person2 = new PersonWithConverter();
            person2.setName(name2);
            PersonWithConverter person3 = new PersonWithConverter();
            person3.setName(name3);

            String expectedResponse= person1 + "," + person2 + "," + person3;

            final MultivaluedMap<String, Object> people = new MultivaluedMapImpl<>();
            people.add("person", name1);
            people.add("person", name2);
            people.add("person", name3);

            Response response3 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_list")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_arrayList")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_set")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_hashSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_sortedSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_treeSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/queryParam/customConversionCdi_array")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }
}
