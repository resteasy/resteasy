package org.jboss.resteasy.test.providers.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jakarta.activation.DataSource;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.DataSourceProvider;
import org.jboss.resteasy.test.providers.datasource.resource.DataSourceProviderInputStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for https://issues.jboss.org/browse/RESTEASY-779
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        Client client = ClientBuilder.newClient();
        client.register(DataSourceProvider.class);
        WebTarget target = client.target(generateURL("/"));
        int expectedLength = DataSourceProviderInputStreamResource.KBs * 1024;

        // as DataSource
        Response response = target.request().get();
        DataSource dataSource = response.readEntity(DataSource.class);
        int length = TestUtil.readString(dataSource.getInputStream()).length();
        logger.info(String.format("Length as DataSource: %d", length));
        Assertions.assertEquals(expectedLength, length, "Wrong length of response");

        // as String
        response = target.request().get();
        String string = response.readEntity(String.class);
        length = string.length();
        logger.info(String.format("Length as String: %d", length));
        Assertions.assertEquals(expectedLength, length, "Wrong length of response");

        // as InputStream
        response = target.request().get();
        InputStream inputStream = response.readEntity(InputStream.class);
        dataSource = DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);
        length = TestUtil.readString(dataSource.getInputStream()).length();
        logger.info(String.format("Length as InputStream: %d", length));
        Assertions.assertEquals(expectedLength, length, "Wrong length of response");

        client.close();
    }

    /**
     * @tpTestDetails Check DataSource provider with Apache client.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDataSourceProvider() throws Exception {
        ConnectionConfig config = ConnectionConfig.custom()
                .setBufferSize((DataSourceProviderInputStreamResource.KBs - 1) * 1024).build();
        HttpClient httpClient = HttpClientBuilder.create().setDefaultConnectionConfig(config).build();
        HttpGet httpGet = new HttpGet(generateURL("/"));
        HttpResponse response = httpClient.execute(httpGet);
        try (InputStream inputStream = response.getEntity().getContent()) {
            DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);
            assertEquals(0,
                    findSizeOfRemainingDataInStream(inputStream),
                    "DataSourceProvider does not properly read InputStream");
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

    @AfterAll
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
