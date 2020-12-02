package org.jboss.resteasy.test.providers.jsonb.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.Dog;
import org.jboss.resteasy.test.providers.jsonb.basic.resource.SetMethodWithMoreArgumentsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

/**
 * @tpSubChapter Json-binding provider
 * @tpChapter Integration test
 * @tpSince RESTEasy 3.6.2.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SetMethodWithMoreArgumentsTest {

   protected static final Logger LOG = Logger.getLogger(SetMethodWithMoreArgumentsTest.class.getName());

   static Client client;

   private static final String DEFAULT = "war_default";

   @Deployment(name = DEFAULT)
   public static Archive<?> deployDefault() {
      WebArchive war = TestUtil.prepareArchive(DEFAULT);
      war.addClass(Dog.class);
      return TestUtil.finishContainerPrepare(war, null, SetMethodWithMoreArgumentsResource.class);
   }


   @BeforeClass
   public static void init() {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   /**
    * @tpTestDetails Entity class Dog has setNameAndSort method with two arguments which causes 'javax.json.bind.JsonbException: Invalid count of arguments for setter' with yasson-1.0.1.
    * This test checks that this behavior is no longer present in newer versions of yasson.
    * @tpSince RESTEasy 3.6.2.Final
    */
   @Test
   public void test() {
      WebTarget target = client.target(PortProviderUtil.generateURL("/dog", DEFAULT));
      Entity<Dog> entity = Entity.entity(
            new Dog("Rex", "german shepherd"), MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8"));
      Dog dog = target.request().post(entity, Dog.class);
      LOG.info(dog);
      Assert.assertTrue(dog.getName().equals("Jethro"));
      Assert.assertTrue(dog.getSort().equals("stafford"));
   }

}
