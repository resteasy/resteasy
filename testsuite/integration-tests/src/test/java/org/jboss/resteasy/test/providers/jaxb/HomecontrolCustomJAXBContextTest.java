package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.HomecontrolCustomJAXBContext;
import org.jboss.resteasy.test.providers.jaxb.resource.HomecontrolApplication;
import org.jboss.resteasy.test.providers.jaxb.resource.HomecontrolService;
import org.jboss.resteasy.test.providers.jaxb.resource.HomecontrolJaxbProvider;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.Base64Binary;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.BinaryType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ErrorDomainType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ErrorMessageType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.IDType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ObjectFactory;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.ErrorType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.RoleType;
import org.jboss.resteasy.test.providers.jaxb.resource.homecontrol.UserType;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.5
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HomecontrolCustomJAXBContextTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(HomecontrolCustomJAXBContextTest.class.getSimpleName());
      war.addClasses(HomecontrolCustomJAXBContext.class,
              HomecontrolApplication.class,
              HomecontrolService.class,
              HomecontrolJaxbProvider.class,
              ObjectFactory.class,
              ErrorDomainType.class,
              BinaryType.class,
              ErrorType.class,
              Base64Binary.class,
              RoleType.class,
              UserType.class,
              IDType.class,
              ErrorMessageType.class
      );
      war.addAsWebInfResource(HomecontrolCustomJAXBContextTest.class.getPackage(), "homecontrol/web.xml");
      return TestUtil.finishContainerPrepare(war, null, HomecontrolCustomJAXBContextTest.class);
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
      return PortProviderUtil.generateURL(path, HomecontrolCustomJAXBContextTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test that a user provided JAXBContext implementation is use.
    * @tpInfo RESTEASY-1754
    * @tpSince RESTEasy 3.5
    */
   @Test
   public void testMarshallering() throws Exception {

      String xmlStr = "<user xmlns=\"http://creaity.de/homecontrol/rest/types/v1\"> <id>id</id>"
              + " <credentials> <loginId>test</loginId> </credentials>"
              + " <roles><role>USER</role></roles></user>";

      ResteasyWebTarget target = client.target(generateURL("/service/users"));
      Response response = target.request().accept("application/xml").post(Entity.xml(xmlStr));
      UserType entity = response.readEntity(UserType.class);
      Assert.assertNotNull(entity);
      Assert.assertTrue("id DemoService_visited".equals(entity.getId()));
      response.close();
   }
}
