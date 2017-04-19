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
import org.jboss.resteasy.test.resource.param.resource.DateListParamConverter;
import org.jboss.resteasy.test.resource.param.resource.DateParamConverter;
import org.jboss.resteasy.test.resource.param.resource.DateParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultResource;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJO;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJOConverter;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJOConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterResource;
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
 * @tpTestCaseDetails Test for ParamConverter
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ParamConverterTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ParamConverterTest.class.getSimpleName());
        war.addClass(ParamConverterPOJOConverter.class);
        war.addClass(ParamConverterPOJO.class);
        war.addClass(DateParamConverter.class);
        war.addClass(DateListParamConverter.class);
        war.addClass(ParamConverterDefaultClient.class);
        war.addClass(ParamConverterClient.class);
        return TestUtil.finishContainerPrepare(war, null, ParamConverterPOJOConverterProvider.class, DateParamConverterProvider.class,
                ParamConverterResource.class, ParamConverterDefaultResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ParamConverterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
    public void testIt() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			ParamConverterClient paramConverterClient = client.target(generateBaseUrl()).proxy(ParamConverterClient.class);
			paramConverterClient.put("pojo", "pojo", "pojo", "pojo");
			
			String date1 = "20161217";
			String date2 = "20161218";
			String date3 = "20161219";
			
			Assert.assertEquals(date1 + ", " + date2, paramConverterClient.multiValuedQueryParam(date1 + "," + date2));
			
			List<String> dates=Arrays.asList(date1, date2, date3);
			String expectedResponse= date1 + ", " + date2 + ", " + date3;
			Assert.assertEquals(expectedResponse, paramConverterClient.singleValuedQueryParam_list(dates));
			Assert.assertEquals(expectedResponse, paramConverterClient.singleValuedQueryParam_arrayList(new ArrayList<>(dates)));
			
			
			Assert.assertTrue(dates.containsAll(Arrays.asList(paramConverterClient.singleValuedQueryParam_set(new HashSet<>(dates)).split(", "))));
			Assert.assertTrue(dates.containsAll(Arrays.asList(paramConverterClient.singleValuedQueryParam_hashSet(new HashSet<>(dates)).split(", "))));
			
			Assert.assertEquals(expectedResponse, paramConverterClient.singleValuedQueryParam_sortedSet(new TreeSet<>(dates)));
			Assert.assertEquals(expectedResponse, paramConverterClient.singleValuedQueryParam_treeSet(new TreeSet<>(dates)));
		} finally {
			client.close();
		}
    }

    /**
     * @tpTestDetails Check default values
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
	public void testDefault() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			ParamConverterDefaultClient proxy = client.target(generateBaseUrl()).proxy(ParamConverterDefaultClient.class);
			proxy.put();
			Assert.assertEquals("20161214, 20161215, 20161216", proxy.multiValuedQueryParam());
			
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_list());
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_arrayList());
			
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_set());
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_hashSet());
			
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_sortedSet());
			Assert.assertEquals("20161214", proxy.singleValuedQueryParam_treeSet());
		} finally {
			client.close();
		}
	}
    
}
