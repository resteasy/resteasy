package org.jboss.resteasy.test.providers.html;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.html.resource.HtmlSanitizerOptionalResource;
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
 * @tpSubChapter
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2034

 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HtmlSanitizerOptionalTest {

   static ResteasyClient client;

   private static final String ENABLED  = "_enabled";
   private static final String DISABLED = "_disabled";
   private static final String DEFAULT  = "_default";

   static public  final String input = "<html &lt;\"abc\" 'xyz'&gt;/>";
   static private final String output = "&lt;html &amp;lt;&quot;abc&quot; &#x27;xyz&#x27;&amp;gt;&#x2F;&gt;";

   @Deployment(name = ENABLED, order = 1)
   public static Archive<?> createTestArchive1() {
      WebArchive war = TestUtil.prepareArchive(HtmlSanitizerOptionalTest.class.getSimpleName() + ENABLED);
      war.addAsWebInfResource(HtmlSanitizerOptionalTest.class.getPackage(), "HtmlSanitizerOptional_Enabled_web.xml", "web.xml");
      return TestUtil.finishContainerPrepare(war, null, HtmlSanitizerOptionalResource.class);
   }

   @Deployment(name = DISABLED, order = 2)
   public static Archive<?> createTestArchive2() {
      WebArchive war = TestUtil.prepareArchive(HtmlSanitizerOptionalTest.class.getSimpleName() + DISABLED);
      war.addAsWebInfResource(HtmlSanitizerOptionalTest.class.getPackage(), "HtmlSanitizerOptional_Disabled_web.xml", "web.xml");
      return TestUtil.finishContainerPrepare(war, null, HtmlSanitizerOptionalResource.class);
   }

   @Deployment(name = DEFAULT, order = 3)
   public static Archive<?> createTestArchive3() {
      WebArchive war = TestUtil.prepareArchive(HtmlSanitizerOptionalTest.class.getSimpleName() + DEFAULT);
      war.addAsWebInfResource(HtmlSanitizerOptionalTest.class.getPackage(), "HtmlSanitizerOptional_Default_web.xml", "web.xml");
      return TestUtil.finishContainerPrepare(war, null, HtmlSanitizerOptionalResource.class);
   }

   private String generateURL(String path, String version) {
      return PortProviderUtil.generateURL(path, HtmlSanitizerOptionalTest.class.getSimpleName() + version);
   }

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Context parameter "resteasy.disable.html.sanitizer" is set to "true".
    * @tpPassCrit Input string should be unchanged.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testHtmlSanitizerDisabled() throws Exception {
      Response response = client.target(generateURL("/test", DISABLED)).request().get();
      Assert.assertEquals(input, response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Context parameter "resteasy.disable.html.sanitizer" is set to "false"
    * @tpPassCrit Input string should be sanitized.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testHtmlSanitizerEnabled() throws Exception {
      Response response = client.target(generateURL("/test", ENABLED)).request().get();
      Assert.assertEquals(output, response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Context parameter "resteasy.disable.html.sanitizer" is not set.
    * @tpPassCrit Input string should be sanitized.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testHtmlSanitizerDefault() throws Exception {
      Response response = client.target(generateURL("/test", DEFAULT)).request().get();
      Assert.assertEquals(output, response.readEntity(String.class));
   }
}
