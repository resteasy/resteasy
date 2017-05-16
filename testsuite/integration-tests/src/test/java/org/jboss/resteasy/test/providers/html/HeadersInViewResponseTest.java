package org.jboss.resteasy.test.providers.html;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.html.resource.HeadersInViewResponseResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter HTML provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.3.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HeadersInViewResponseTest {

    static ResteasyClient client;

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HeadersInViewResponseTest.class.getSimpleName());
        war.addAsLibrary(getResteasyHtmlJar());
        return TestUtil.finishContainerPrepare(war, null, HeadersInViewResponseResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
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
    @Category(NotForForwardCompatibility.class)
    public void testView() throws Exception {
       
       Invocation.Builder request = client.target(generateURL("/test/get")).request();
       Response response = request.get();
       Map<String, NewCookie> map = response.getCookies();
       Assert.assertEquals("123", response.getHeaderString("abc"));
       Assert.assertEquals("value1", map.get("name1").getValue());
       Assert.assertEquals("789", response.getHeaderString("xyz"));
       Assert.assertEquals("value2", map.get("name2").getValue());
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
       return TestUtil.resolveDependency("org.jboss.resteasy:resteasy-html:" + version);
    }
}
