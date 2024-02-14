package org.jboss.resteasy.test.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.stream.Collectors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.client.resource.HeaderEmptyHostResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * RESTEASY-2300 and UNDERTOW-1614
 *
 * @author <a href="mailto:istudens@redhat.com">Ivo Studensky</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HeaderEmptyHostTest extends ClientTestBase {
    private static Logger logger = Logger.getLogger(HeaderEmptyHostTest.class);

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(HeaderEmptyHostTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, HeaderEmptyHostResource.class);
    }

    @ArquillianResource
    URL url;

    @Test
    public void testEmptyHost() throws Exception {
        try (Socket client = new Socket(url.getHost(), url.getPort())) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                final String uri = "/HeaderEmptyHostTest/headeremptyhostresource";
                out.printf("GET %s HTTP/1.1\r\n", uri);
                out.print("Host: \r\n");
                out.print("Connection: close\r\n");
                out.print("\r\n");
                out.flush();
                String response = new BufferedReader(new InputStreamReader(client.getInputStream())).lines()
                        .collect(Collectors.joining("\n"));
                logger.info("response = " + response);
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.contains("HTTP/1.1 200 OK"));
                Assertions.assertTrue(response.contains("uriInfo: http://" + url.getHost() + ":" + url.getPort() + uri));
            }
        }
    }

}
