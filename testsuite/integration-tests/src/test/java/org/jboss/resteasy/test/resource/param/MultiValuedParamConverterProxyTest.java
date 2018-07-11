package org.jboss.resteasy.test.resource.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.*;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamCustomConversionResourceClient.QueryParamResourceClient;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.resteasy.test.resource.param.resource.MultiValuedParamCustomConversionResourceClient.*;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param
 * @tpSince RESTEasy 3.5.1.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamConverterProxyTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamConverterProxyTest.class.getSimpleName());
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
        war.addClass(MultiValuedParamCustomConversionResourceClient.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedPersonParamConverterProvider.class,
                MultiValuedParamCustomConversionResource.class,  MultiValuedParamCustomConversionResource.QueryParamResource.class,
                MultiValuedParamCustomConversionResource.HeaderParamResource.class, MultiValuedParamCustomConversionResource.MatrixParamResource.class,
                MultiValuedParamCustomConversionResource.CookieParamResource.class, MultiValuedParamCustomConversionResource.PathParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamConverterProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testQueryParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            QueryParamResourceClient queryParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamCustomConversionResourceClient.class).queryParam();
            
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
            String people = name1 + "," + name2 + "," + name3;
            
            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_list(people));
            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_arrayList(people));

            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_set(people));
            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_hashSet(people));

            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_sortedSet(people));
            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_treeSet(people));

            Assert.assertEquals(expectedResponse, queryParamResourceClient.customConversion_array(people));
        } finally {
            client.close();
        }
    }


    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testHeaderParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            HeaderParamResourceClient headerParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamCustomConversionResourceClient.class).headerParam();

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
            String people = name1 + "," + name2 + "," + name3;

            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_list(people));
            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_arrayList(people));

            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_set(people));
            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_hashSet(people));

            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_sortedSet(people));
            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_treeSet(people));

            Assert.assertEquals(expectedResponse, headerParamResourceClient.customConversion_array(people));
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testMatrixParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            MatrixParamResourceClient matrixParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamCustomConversionResourceClient.class).matrixParam();

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
            String people = name1 + "," + name2 + "," + name3;

            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_list(people));
            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_arrayList(people));

            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_set(people));
            Assert.assertEquals(expectedResponse,matrixParamResourceClient.customConversion_hashSet(people));

            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_sortedSet(people));
            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_treeSet(people));

            Assert.assertEquals(expectedResponse, matrixParamResourceClient.customConversion_array(people));
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testCookieParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            CookieParamResourceClient cookieParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamCustomConversionResourceClient.class).cookieParam();

            String name1 = "George";
            PersonWithConverter person1 = new PersonWithConverter();
            person1.setName(name1);

            String expectedResponse= person1.toString();

            String people = name1;

            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_list(people));
            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_arrayList(people));

            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_set(people));
            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_hashSet(people));

            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_sortedSet(people));
            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_treeSet(people));

            Assert.assertEquals(expectedResponse, cookieParamResourceClient.customConversion_array(people));
        } finally {
            client.close();
        }
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testPathParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            PathParamResourceClient pathParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamCustomConversionResourceClient.class).pathParam();

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
            String people = name1 + "," + name2 + "," + name3;

            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_list(people));
            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_arrayList(people));

            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_set(people));
            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_hashSet(people));

            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_sortedSet(people));
            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_treeSet(people));

            Assert.assertEquals(expectedResponse, pathParamResourceClient.customConversion_array(people));
        } finally {
            client.close();
        }
    }

}
