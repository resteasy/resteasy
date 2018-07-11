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
public class MultiValuedParamConverterTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamConverterTest.class.getSimpleName());
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
                MultiValuedParamCustomConversionResource.class,  MultiValuedParamCustomConversionResource.QueryParamResource.class,
                MultiValuedParamCustomConversionResource.HeaderParamResource.class, MultiValuedParamCustomConversionResource.MatrixParamResource.class,
                MultiValuedParamCustomConversionResource.CookieParamResource.class, MultiValuedParamCustomConversionResource.PathParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamConverterTest.class.getSimpleName());
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

            Response response3 = client.target(generateBaseUrl() + "/queryParam/customConversion_list")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/queryParam/customConversion_arrayList")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/queryParam/customConversion_set")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/queryParam/customConversion_hashSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/queryParam/customConversion_sortedSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/queryParam/customConversion_treeSet")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/queryParam/customConversion_array")
                    .queryParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }


    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5.1
     */
    @Test
    public void testHeaderParam() throws Exception {
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

            Response response3 = client.target(generateBaseUrl() + "/headerParam/customConversion_list")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/headerParam/customConversion_arrayList")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/headerParam/customConversion_set")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/headerParam/customConversion_hashSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/headerParam/customConversion_sortedSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/headerParam/customConversion_treeSet")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/headerParam/customConversion_array")
                    .request().header("person",name1 + "," + name2 + "," + name3).get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5.1
     */
    @Test
    public void testMatrixParam() throws Exception {
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

            Response response3 = client.target(generateBaseUrl() + "/matrixParam/customConversion_list")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/matrixParam/customConversion_arrayList")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/matrixParam/customConversion_set")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/matrixParam/customConversion_hashSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/matrixParam/customConversion_sortedSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/matrixParam/customConversion_treeSet")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/matrixParam/customConversion_array")
                    .matrixParam("person",name1 + "," + name2 + "," + name3).request().get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5.1
     */
    @Test
    public void testCookieParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            String name1 = "George";
            PersonWithConverter person1 = new PersonWithConverter();
            person1.setName(name1);

            String expectedResponse= person1.toString();

            Response response3 = client.target(generateBaseUrl() + "/cookieParam/customConversion_list")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/cookieParam/customConversion_arrayList")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/cookieParam/customConversion_set")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/cookieParam/customConversion_hashSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/cookieParam/customConversion_sortedSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/cookieParam/customConversion_treeSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/cookieParam/customConversion_array")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5.1
     */
    @Test
    public void testPathParam() throws Exception {
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

            String expectedResponse = person1 + "," + person2 + "," + person3;

            Response response3 = client.target(generateBaseUrl() + "/pathParam/customConversion_list/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/pathParam/customConversion_arrayList/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/pathParam/customConversion_set/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/pathParam/customConversion_hashSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/pathParam/customConversion_sortedSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/pathParam/customConversion_treeSet/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


            Response response9 = client.target(generateBaseUrl() + "/pathParam/customConversion_array/" + name1 + "," + name2 + "," + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response9.readEntity(String.class));

        } finally {
            client.close();
        }
    }

}
