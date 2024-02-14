package org.jboss.resteasy.test.providers.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.DataSourceProvider;
import org.jboss.resteasy.test.providers.datasource.resource.ReadFromSocketDataSourceProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReadFromSocketDataSourceProviderTest {

    protected static final Logger logger = Logger.getLogger(ReadFromSocketDataSourceProviderTest.class.getName());
    static ResteasyClient client;

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ReadFromSocketDataSourceProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ReadFromSocketDataSourceProviderResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ReadFromSocketDataSourceProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read input stream entirely, using socket buffer for reading
     * @tpInfo RESTEASY-779
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReadFromSocketDataSourceProvider() throws Exception {
        // important - see https://issues.jboss.org/browse/RESTEASY-779
        ConnectionConfig connConfig = ConnectionConfig.custom()
                .setBufferSize((ReadFromSocketDataSourceProviderResource.KBs - 1) * 1024)
                .build();
        CloseableHttpClient client = HttpClients.custom().setDefaultConnectionConfig(connConfig).build();
        HttpGet httpGet = new HttpGet(generateURL("/"));
        CloseableHttpResponse response = client.execute(httpGet);
        try (InputStream inputStream = response.getEntity().getContent()) {
            DataSourceProvider.readDataSource(inputStream, MediaType.TEXT_PLAIN_TYPE);
            Assertions.assertEquals(0, findSizeOfRemainingDataInStream(inputStream),
                    "The input stream was not read entirely");
        }
    }

    static int countTempFiles() throws Exception {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File dir = new File(tmpdir);
        int counter = 0;
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("resteasy-provider-datasource")) {
                counter++;
            }
        }
        return counter;
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
