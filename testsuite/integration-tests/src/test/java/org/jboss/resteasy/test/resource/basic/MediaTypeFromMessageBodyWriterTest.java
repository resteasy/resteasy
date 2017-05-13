package org.jboss.resteasy.test.resource.basic;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterListAsText;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterListAsXML;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy server
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test that MessageBodyWriters can be consulted for media type
 * @tpSince RESTEasy 3.1.3.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MediaTypeFromMessageBodyWriterTest {
    
    private static class Target {
       String path;
       String queryName;
       String queryValue;
       Target(String path, String queryName, String queryValue) {
          this.path = path;
          this.queryName = queryName;
          this.queryValue = queryValue;
       }
    }
    
    private static String ACCEPT_CHROME="text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static String ACCEPT_FIREFOX="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static String ACCEPT_IE11="text/html, application/xhtml+xml, */*";
    private static Collection<Target> tgts = new ArrayList<Target>();
    private static Collection<String> accepts = new ArrayList<String>();
    private static ResteasyClient client;
    
    static
    {
       tgts.add(new Target("java.util.TreeSet", null, null));
       tgts.add(new Target("fixed", "type", "text/plain"));
       tgts.add(new Target("fixed", "type", "application/xml"));
       tgts.add(new Target("variants", null, null));
       tgts.add(new Target("variantsObject", null, null));
       accepts.add(ACCEPT_CHROME);
       accepts.add(ACCEPT_FIREFOX);
       accepts.add(ACCEPT_IE11);
       accepts.add("foo/bar,text/plain");
       accepts.add("foo/bar,*/*");
       accepts.add("text/plain");
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MediaTypeFromMessageBodyWriterTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null,
              MediaTypeFromMessageBodyWriterListAsText.class,
              MediaTypeFromMessageBodyWriterListAsXML.class,
              MediaTypeFromMessageBodyWriterResource.class);
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
        return PortProviderUtil.generateURL(path, MediaTypeFromMessageBodyWriterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test that MessageBodyWriters can be consulted for media type
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
    public void test() throws Exception {
       WebTarget base = client.target(generateURL(""));
       Response response = null;
       for (Target tgt : tgts) {
          for (String accept : accepts) {
             if (tgt.queryName != null) {
                response = base.path(tgt.path).queryParam(tgt.queryName, tgt.queryValue).request().header("Accept", accept).get();
             }
             else {
                response = base.path(tgt.path).request().header("Accept", accept).get();  
             }
             Assert.assertEquals(200, response.getStatus());
             String s = response.getHeaderString("X-COUNT");
             Assert.assertNotNull(s);
             response.close();
          }
       }
       client.close();
    }
}
