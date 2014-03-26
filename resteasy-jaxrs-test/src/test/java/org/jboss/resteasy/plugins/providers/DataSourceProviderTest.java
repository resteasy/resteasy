package org.jboss.resteasy.plugins.providers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * see https://issues.jboss.org/browse/RESTEASY-779
 */
public class DataSourceProviderTest extends BaseResourceTest {

    @Path("/")
    public static class BigDataResource {

        public static final int KBs = 5;
        public static final int SIZE =  KBs * 1024;

        @GET
        @Produces("text/plain")
        public String get()
        {
           StringBuffer buffer = new StringBuffer();
           for (int i = 0; i < SIZE; i++) {
               buffer.append("x");
           }
           return buffer.toString();
        }

    }

    @Override
    @Before
    public void before() throws Exception {
        addPerRequestResource(BigDataResource.class);
        super.before();
    }

    @Test
    public void testDataSourceProvider() throws Exception
    {
        HttpParams params = new BasicHttpParams();
        // important - see https://issues.jboss.org/browse/RESTEASY-779
        params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, (BigDataResource.KBs - 1)  * 1024);
        HttpClient httpClient = new DefaultHttpClient(params);
        HttpGet httpGet = new HttpGet(TestPortProvider.generateURL("/"));
        HttpResponse response = httpClient.execute(httpGet);
        InputStream inputStream = null;
        try {
            inputStream = response.getEntity().getContent();
            DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);

            assertEquals(0, findSizeOfRemainingDataInStream(inputStream));

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private int findSizeOfRemainingDataInStream(InputStream inputStream) throws IOException
    {
        byte[] buf = new byte[4 * 1024];
        int bytesRead, totalBytesRead = 0;
        while ((bytesRead = inputStream.read(buf, 0, buf.length)) != -1)
        {
            totalBytesRead += bytesRead;
        }
        return totalBytesRead;
    }

}
