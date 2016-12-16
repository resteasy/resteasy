package org.jboss.resteasy.test.providers.datasource;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.DataSourceProvider;
import org.jboss.resteasy.test.providers.datasource.resource.DataSourceProviderInputStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.activation.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for https://issues.jboss.org/browse/RESTEASY-779
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DataSourceProviderInputStreamTest {

    public static Logger logger = Logger.getLogger(DataSourceProviderInputStreamTest.class);

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(DataSourceProviderInputStreamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, DataSourceProviderInputStreamResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DataSourceProviderInputStreamTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check DataSource provider with RESTEasy client.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDataSourceProviderRestClient() throws Exception {
        Client client = new ResteasyClientBuilder().build();
        client.register(DataSourceProvider.class);
        WebTarget target = client.target(generateURL("/"));
        int expectedLength = DataSourceProviderInputStreamResource.KBs * 1024;

        // as DataSource
        Response response = target.request().get();
        DataSource dataSource = response.readEntity(DataSource.class);
        int length = TestUtil.readString(dataSource.getInputStream()).length();
        logger.info(String.format("Length as DataSource: %d", length));
        Assert.assertEquals("Wrong length of response", expectedLength, length);

        // as String
        response = target.request().get();
        String string = response.readEntity(String.class);
        length = string.length();
        logger.info(String.format("Length as String: %d", length));
        Assert.assertEquals("Wrong length of response", expectedLength, length);

        // as InputStream
        response = target.request().get();
        InputStream inputStream = response.readEntity(InputStream.class);
        dataSource = DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);
        length = TestUtil.readString(dataSource.getInputStream()).length();
        logger.info(String.format("Length as InputStream: %d", length));
        Assert.assertEquals("Wrong length of response", expectedLength, length);

        client.close();
    }

    /**
     * @tpTestDetails Check DataSource provider with Apache client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDataSourceProvider() throws Exception {
        ConnectionConfig config = ConnectionConfig.custom().setBufferSize((DataSourceProviderInputStreamResource.KBs - 1) * 1024).build();
        HttpClient httpClient = HttpClientBuilder.create().setDefaultConnectionConfig(config).build();
        HttpGet httpGet = new HttpGet(generateURL("/"));
        HttpResponse response = httpClient.execute(httpGet);
        InputStream inputStream = null;
        try {
            inputStream = response.getEntity().getContent();
            DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);
            assertEquals("DataSourceProvider does not properly read InputStream", 0, findSizeOfRemainingDataInStream(inputStream));
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private int findSizeOfRemainingDataInStream(InputStream inputStream) throws IOException {
        byte[] buf = new byte[4 * 1024];
        int bytesRead, totalBytesRead = 0;
        while ((bytesRead = inputStream.read(buf, 0, buf.length)) != -1) {
            totalBytesRead += bytesRead;
        }
        return totalBytesRead;
    }

    @AfterClass
    public static void afterClass() {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File dir = new File(tmpdir);
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("resteasy-provider-datasource")) {
                file.delete();
            }
        }
    }

}
