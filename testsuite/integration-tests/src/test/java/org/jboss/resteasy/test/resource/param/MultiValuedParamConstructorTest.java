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
public class MultiValuedParamConstructorTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamConstructorTest.class.getSimpleName());
        war.addClass(MultiValuedParam.class);
        war.addClass(ParamWrapper.class);
        war.addClass(MultiValuedCookieParam.class);
        war.addClass(CookieParamWrapper.class);
        war.addClass(MultiValuedPathParam.class);
        war.addClass(PathParamWrapper.class);
        war.addClass(PersonWithConstructor.class);
        war.addClass(PersonWithValueOf.class);
        war.addClass(PersonWithFromString.class);
        war.addClass(DateParamConverter.class);
        war.addClass(MultiValuedParamConverter.class);
        war.addClass(ParamWrapperArrayConverter.class);
        war.addClass(MultiValuedCookieParamConverter.class);
        war.addClass(CookieParamWrapperArrayConverter.class);
        war.addClass(MultiValuedPathParamConverter.class);
        war.addClass(PathParamWrapperArrayConverter.class);
        war.addClass(MultiValuedParamResourceClient.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedParamConverterProvider.class,
                MultiValuedParamDefaultConversionResource.class,  MultiValuedParamDefaultConversionResource.QueryParamResource.class, MultiValuedParamDefaultConversionResource.HeaderParamResource.class,
                MultiValuedParamDefaultConversionResource.PathParamResource.class, MultiValuedParamDefaultConversionResource.CookieParamResource.class,
                MultiValuedParamDefaultConversionResource.MatrixParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamConstructorTest.class.getSimpleName());
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
            PersonWithConstructor person1 = new PersonWithConstructor(name1);
            PersonWithConstructor person2 = new PersonWithConstructor(name2);
            PersonWithConstructor person3 = new PersonWithConstructor(name3);

            String expectedResponse= person1 + "," + person2 + "," + person3;

            final MultivaluedMap<String, Object> people = new MultivaluedMapImpl<>();
            people.add("person", name1);
            people.add("person", name2);
            people.add("person", name3);

            Response response3 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_list")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_arrayList")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_set")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_hashSet")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_sortedSet")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/queryParam/defaultConversionConstructor_treeSet")
                    .queryParams(people).request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));

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
            PersonWithConstructor person1 = new PersonWithConstructor(name1);
            PersonWithConstructor person2 = new PersonWithConstructor(name2);
            PersonWithConstructor person3 = new PersonWithConstructor(name3);

            String expectedResponse= person1 + "," + person2 + "," + person3;

            final MultivaluedMap<String, Object> people = new MultivaluedMapImpl<>();
            people.add("person", name1);
            people.add("person", name2);
            people.add("person", name3);

            Response response3 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_list")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_arrayList")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_set")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_hashSet")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_sortedSet")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/headerParam/defaultConversionConstructor_treeSet")
                    .request().headers(people).get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


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
            PersonWithConstructor person1 = new PersonWithConstructor(name1);
            PersonWithConstructor person2 = new PersonWithConstructor(name2);
            PersonWithConstructor person3 = new PersonWithConstructor(name3);

            String expectedResponse= person1 + "," + person2 + "," + person3;

            final MultivaluedMap<String, Object> people = new MultivaluedMapImpl<>();
            people.add("person", name1);
            people.add("person", name2);
            people.add("person", name3);

            Response response3 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_list")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_arrayList")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_set")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_hashSet")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_sortedSet")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/matrixParam/defaultConversionConstructor_treeSet")
                    .matrixParam("person", name1).matrixParam("person", name2).matrixParam("person", name3).request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));



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
            PersonWithConstructor person1 = new PersonWithConstructor(name1);
            String expectedResponse = person1.toString();

            Response response3 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_list")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_arrayList")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_set")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_hashSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_sortedSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/cookieParam/defaultConversionConstructor_treeSet")
                    .request().cookie("person",name1).get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));



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
            PersonWithConstructor person1 = new PersonWithConstructor(name1);
            PersonWithConstructor person2 = new PersonWithConstructor(name2);
            PersonWithConstructor person3 = new PersonWithConstructor(name3);

            String expectedResponse= person1 + "," + person2 + "," + person3;

            Response response3 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_list/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response3.readEntity(String.class));

            Response response4 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_arrayList/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response4.readEntity(String.class));


            Response response5 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_set/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response5.readEntity(String.class));

            Response response6 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_hashSet/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response6.readEntity(String.class));

            Response response7 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_sortedSet/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response7.readEntity(String.class));

            Response response8 = client.target(generateBaseUrl() + "/pathParam/defaultConversionConstructor_treeSet/" + name1 + "/" + name2 + "/" + name3)
                    .request().get();
            Assert.assertEquals(expectedResponse, response8.readEntity(String.class));


        } finally {
            client.close();
        }
    }

}
