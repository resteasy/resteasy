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
import org.jboss.resteasy.test.resource.param.resource.CookieParamWrapper;
import org.jboss.resteasy.test.resource.param.resource.CookieParamWrapperArrayConverter;
import org.jboss.resteasy.test.resource.param.resource.DateParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedCookieParam;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedCookieParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParam;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.CookieParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.FormParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.HeaderParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.MatrixParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.PathParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamResourceClient.QueryParamResourceClient;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedPathParam;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedPathParamConverter;
import org.jboss.resteasy.test.resource.param.resource.ParamWrapper;
import org.jboss.resteasy.test.resource.param.resource.ParamWrapperArrayConverter;
import org.jboss.resteasy.test.resource.param.resource.PathParamWrapper;
import org.jboss.resteasy.test.resource.param.resource.PathParamWrapperArrayConverter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 *                    java.util.Date class is used
 *                    Client Proxy is used
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MultiValuedParamDateProxyTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamDateProxyTest.class.getSimpleName());
        war.addClass(MultiValuedParam.class);
        war.addClass(ParamWrapper.class);
        war.addClass(MultiValuedCookieParam.class);
        war.addClass(CookieParamWrapper.class);
        war.addClass(MultiValuedPathParam.class);
        war.addClass(PathParamWrapper.class);
        war.addClass(DateParamConverter.class);
        war.addClass(MultiValuedParamConverter.class);
        war.addClass(ParamWrapperArrayConverter.class);
        war.addClass(MultiValuedCookieParamConverter.class);
        war.addClass(CookieParamWrapperArrayConverter.class);
        war.addClass(MultiValuedPathParamConverter.class);
        war.addClass(PathParamWrapperArrayConverter.class);
        war.addClass(MultiValuedParamResourceClient.class);
		return TestUtil.finishContainerPrepare(war, null, MultiValuedParamConverterProvider.class,
				MultiValuedParamResource.class,  MultiValuedParamResource.QueryParamResource.class, MultiValuedParamResource.HeaderParamResource.class,
				MultiValuedParamResource.PathParamResource.class, MultiValuedParamResource.CookieParamResource.class,
				MultiValuedParamResource.MatrixParamResource.class, MultiValuedParamResource.FormParamResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MultiValuedParamDateProxyTest.class.getSimpleName());
    }

	String date1 = "20161217";
	String date2 = "20161218";
	String date3 = "20161219";
	List<String> dates = Arrays.asList(date1, date2, date3);
	String expectedResponse = date1 + "," + date2 + "," + date3;

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testQueryParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			QueryParamResourceClient queryParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).queryParam();
			
			Assert.assertEquals(date1 + "," + date2 + "," + date3, queryParamResourceClient.customConversion_multiValuedParam(date1 + "," + date2 + "," + date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, queryParamResourceClient.customConversion_multiValuedParam_array(date1 + "," + date2 + "," + date3));

			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_list(dates));
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_arrayList(new ArrayList<>(dates)));
			
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_set(new HashSet<>(dates)));
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_hashSet(new HashSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_sortedSet(new TreeSet<>(dates)));
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_treeSet(new TreeSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, queryParamResourceClient.defaultConversion_array(dates.toArray(new String[dates.size()])));
		} finally {
			client.close();
		}
    }
    

    /**
     * @tpTestDetails HeaderParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeaderParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			HeaderParamResourceClient headerParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).headerParam();
			
			Assert.assertEquals(date1 + "," + date2 + "," + date3, headerParamResourceClient.customConversion_multiValuedParam(date1 + "," + date2 + "," + date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, headerParamResourceClient.customConversion_multiValuedParam_array(date1 + "," + date2 + "," + date3));
			
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_list(dates));
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_arrayList(new ArrayList<>(dates)));
			
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_set(new HashSet<>(dates)));
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_hashSet(new HashSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_sortedSet(new TreeSet<>(dates)));
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_treeSet(new TreeSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, headerParamResourceClient.defaultConversion_array(dates.toArray(new String[dates.size()])));
		} finally {
			client.close();
		}
    }
    
    /**
     * @tpTestDetails MatrixParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testMatrixParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			MatrixParamResourceClient matrixParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).matrixParam();
			
			Assert.assertEquals(date1 + "," + date2 + "," + date3, matrixParamResourceClient.customConversion_multiValuedParam(date1 + "," + date2 + "," + date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, matrixParamResourceClient.customConversion_multiValuedParam_array(date1 + "," + date2 + "," + date3));
			
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_list(dates));
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_arrayList(new ArrayList<>(dates)));
			
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_set(new HashSet<>(dates)));
			Assert.assertEquals(expectedResponse,matrixParamResourceClient.defaultConversion_hashSet(new HashSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_sortedSet(new TreeSet<>(dates)));
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_treeSet(new TreeSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, matrixParamResourceClient.defaultConversion_array(dates.toArray(new String[dates.size()])));
		} finally {
			client.close();
		}
    }

    /**
     * @tpTestDetails CoookieParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testCookieParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			CookieParamResourceClient cookieParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).cookieParam();
			
			Assert.assertEquals(date1 + "," + date2 + "," + date3, cookieParamResourceClient.customConversion_multiValuedCookieParam(date1 + "-" + date2 + "-" + date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, cookieParamResourceClient.customConversion_multiValuedCookieParam_array(date1 + "-" + date2 + "-" + date3));
			
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_list(date1));
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_arrayList(date1));
			
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_set(date1));
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_hashSet(date1));
			
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_sortedSet(date1));
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_treeSet(date1));
			
			Assert.assertEquals(date1, cookieParamResourceClient.defaultConversion_array(date1));
		} finally {
			client.close();
		}
    }
    
    /**
     * @tpTestDetails FormParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testFormParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			FormParamResourceClient formParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).formParam();
			
			Assert.assertEquals(date1 + "," + date2 + "," + date3, formParamResourceClient.customConversion_multiValuedParam(date1 + "," + date2 + "," + date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, formParamResourceClient.customConversion_multiValuedParam_array(date1 + "," + date2 + "," + date3));
			
			String expectedResponse= date1 + "," + date2 + "," + date3;
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_list(dates));
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_arrayList(new ArrayList<>(dates)));
			
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_set(new HashSet<>(dates)));
			Assert.assertEquals(expectedResponse,formParamResourceClient.defaultConversion_hashSet(new HashSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_sortedSet(new TreeSet<>(dates)));
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_treeSet(new TreeSet<>(dates)));
			
			Assert.assertEquals(expectedResponse, formParamResourceClient.defaultConversion_array(dates.toArray(new String[dates.size()])));
		} finally {
			client.close();
		}
    }
    
    /**
     * @tpTestDetails PathParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testPathParam() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			PathParamResourceClient pathParamResourceClient = client.target(generateBaseUrl()).proxy(MultiValuedParamResourceClient.class).pathParam();

			Assert.assertEquals(date1 + "," + date2 + "," + date3, pathParamResourceClient.customConversion_multiValuedPathParam(date1, date2, date3));
			Assert.assertEquals(date1 + "," + date2 + "," + date3, pathParamResourceClient.customConversion_multiValuedPathParam_array(date1, date2, date3));
			
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_list(date1));
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_arrayList(date1));
			
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_set(date1));
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_hashSet(date1));
			
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_sortedSet(date1));
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_treeSet(date1));
			
			Assert.assertEquals(date1, pathParamResourceClient.defaultConversion_array(date1));
		} finally {
			client.close();
		}
    }
    
}
