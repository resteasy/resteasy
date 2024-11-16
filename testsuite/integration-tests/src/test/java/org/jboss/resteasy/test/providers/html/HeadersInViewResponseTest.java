package org.jboss.resteasy.test.providers.html;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.html.resource.HeadersInViewResponseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter HTML provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.3.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class HeadersInViewResponseTest {

    static ResteasyClient client;

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HeadersInViewResponseTest.class.getSimpleName());
        war.addAsLibrary(getResteasyHtmlJar());
        return TestUtil.finishContainerPrepare(war, null, HeadersInViewResponseResource.class);
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
        return PortProviderUtil.generateURL(path, HeadersInViewResponseTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests HTTP headers set in Response with View entity.
     * @tpInfo RESTEASY-1422
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
    public void testView() throws Exception {

        Invocation.Builder request = client.target(generateURL("/test/get")).request();
        Response response = request.get();
        Map<String, NewCookie> map = response.getCookies();
        Assertions.assertEquals("123", response.getHeaderString("abc"));
        Assertions.assertEquals("value1", map.get("name1").getValue());
        Assertions.assertEquals("789", response.getHeaderString("xyz"));
        Assertions.assertEquals("value2", map.get("name2").getValue());
    }

    private static File getResteasyHtmlJar() {

        // Find resteasy-html jar in target
        Path path = Paths.get("..", "..", "providers", "resteasy-html", "target");
        String s = path.toAbsolutePath().toString();
        File dir = new File(s);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                String name = file.getName();
                if (name.startsWith("resteasy-html") && name.endsWith(".jar") && !name.contains("sources")) {
                    return file;
                }
            }
        }

        // If not found in target, try repository
        String version = System.getProperty("project.version");
        return Maven.resolver().resolve("org.jboss.resteasy:resteasy-html:" + version).withoutTransitivity().asSingleFile();
    }
}
