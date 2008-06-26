/**
 * 
 */
package org.resteasy.test.providers.iioimage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.resteasy.test.BaseResourceTest;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 23,
 *         2008
 * 
 */
public class TestIIOImageProvider extends BaseResourceTest {

    private static final String TEST_URI = "http://localhost:8081/image";

    private static final String SRC_ROOT = "./src/test/test-data/";

    private static final String OUTPUT_ROOT = "./target/test-data/";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	File folder = new File(OUTPUT_ROOT);
	folder.mkdir();
	if (!folder.exists()) {
	    folder.mkdir();
	}
	
	addPerRequestResource(ImageResource.class);
	
    }

    /**
     * Test a post of a JPEG image whose response should be a PNG version of the
     * same photo.
     * 
     * @throws Exception
     */
    @Test
    public void testPostJPEGIMage() throws Exception {
	HttpClient client = new HttpClient();
	File file = new File(SRC_ROOT + "harper.jpg");
	Assert.assertTrue(file.exists());
	PostMethod method = new PostMethod(TEST_URI);
	method.setRequestEntity(new FileRequestEntity(file, "image/jpeg"));
	int status = client.executeMethod(method);
	Assert.assertEquals(HttpServletResponse.SC_OK, status);
	InputStream response = method.getResponseBodyAsStream();
	BufferedInputStream in = new BufferedInputStream(response);
	String contentType = method.getResponseHeader("content-type")
		.getValue();
	Assert.assertEquals("image/png", contentType);
	File outFile = new File(OUTPUT_ROOT + "harper.png");
	FileOutputStream out = new FileOutputStream(outFile);
	writeTo(in,out);
	Assert.assertTrue(outFile.exists());
	method.releaseConnection();
    }

    /**
     * Tests a image format that is not directly supported by Image IO. In this
     * case, an HD Photo image is posted to the Resource which should return a 
     * 406 - Not Acceptable response. The response body should include a list of
     * variants that are supported by the application.
     * 
     * @throws Exception
     */
    @Test
    public void testPostUnsupportedImage() throws Exception {
	HttpClient client = new HttpClient();
	File file = new File(SRC_ROOT + "harper.wdp");
	Assert.assertTrue(file.exists());
	PostMethod method = new PostMethod(TEST_URI);
	method.setRequestEntity(new FileRequestEntity(file, "image/vnd.ms-photo"));
	int status = client.executeMethod(method);
	//Assert.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, status);
	Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, status);
    }

    public void writeTo(final InputStream in, final OutputStream out)
	    throws IOException {
	int read;
	final byte[] buf = new byte[2048];
	while ((read = in.read(buf)) != -1) {
	    out.write(buf, 0, read);
	}
    }

}
