/**
 * 
 */
package org.resteasy.test.providers.datasource;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.resteasy.test.BaseResourceTest;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 23,
 *         2008
 * 
 */
public class TestDataSourceProvider extends BaseResourceTest {

    private static final String TEST_URI = "http://localhost:8081/jaf";
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        addPerRequestResource(DataSourceResource.class);
    }
   
    @Test
    public void testPostDataSource() throws Exception {
	HttpClient client = new HttpClient();
	File file = new File("./src/test/test-data/harper.jpg");
	Assert.assertTrue(file.exists());
	PostMethod method = new PostMethod(TEST_URI);
	method.setRequestEntity(new FileRequestEntity(file,"image/jpeg"));
	int status = client.executeMethod(method);
        Assert.assertEquals(HttpServletResponse.SC_OK, status);
        Assert.assertEquals("image/jpeg", method.getResponseBodyAsString());
        method.releaseConnection();
    }

    @Test
    public void testGetDataSource() throws Exception {
	HttpClient client = new HttpClient();
	String value = "foo";
        GetMethod method = new GetMethod(TEST_URI+"/"+value);
        int status = client.executeMethod(method);
        Assert.assertEquals(HttpServletResponse.SC_OK, status);
        Assert.assertEquals(value, method.getResponseBodyAsString());
        method.releaseConnection();
    }
}
