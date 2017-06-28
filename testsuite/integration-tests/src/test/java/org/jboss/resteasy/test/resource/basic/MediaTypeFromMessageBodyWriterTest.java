package org.jboss.resteasy.test.resource.basic;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.custom.resource.CustomProviderPreferenceUser;
import org.jboss.resteasy.test.providers.custom.resource.MediaTypeFromMessageBodyWriterResource2;
import org.jboss.resteasy.test.providers.custom.resource.MediaTypeFromMessageBodyWriterTextJson;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterListAsText;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterListAsXML;
import org.jboss.resteasy.test.resource.basic.resource.MediaTypeFromMessageBodyWriterResource;
import org.jboss.resteasy.util.HttpResponseCodes;
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

    @Deployment(name = "multiple")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MediaTypeFromMessageBodyWriterTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null,
              MediaTypeFromMessageBodyWriterListAsText.class,
              MediaTypeFromMessageBodyWriterListAsXML.class,
              MediaTypeFromMessageBodyWriterResource.class);
    }

    @Deployment(name = "single")
    public static Archive<?> deploy2() {
        WebArchive war = TestUtil.prepareArchive(MediaTypeFromMessageBodyWriterTest.class.getSimpleName() + "_single");
        return TestUtil.finishContainerPrepare(war, null,
                CustomProviderPreferenceUser.class,
                MediaTypeFromMessageBodyWriterTextJson.class,
                MediaTypeFromMessageBodyWriterResource2.class);
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

    private String generateURLUserCase(String path) {
        return PortProviderUtil.generateURL(path, MediaTypeFromMessageBodyWriterTest.class.getSimpleName()+ "_single");
    }

    /**
     * @tpTestDetails Test that MessageBodyWriters can be consulted for media type
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
    @OperateOnDeployment("multiple")
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

    /**
     * @tpTestDetails Test use case described in RESTEASY-1227. If multiple Accept headers are send by the client the chosen
     * response Content-type will be the one which we are able to write (MessageBodyWriter exists for it), not the one
     * we are not able to write.
     * @tpSince RESTEasy 3.1.3.Final
     */
    @Test
    @OperateOnDeployment("single")
    public void test2() throws Exception {
        Response response = client.target(generateURLUserCase("")).request().accept("text/html", "image/jpg", "text/json", "*").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("text/json;charset=UTF-8", response.getHeaderString("Content-type"));
    }
}
