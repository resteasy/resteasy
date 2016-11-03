package org.jboss.resteasy.test.resource.basic;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceCrudResource;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceStudent;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceStudentCrudResource;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceStudentInterface;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceStudentReader;
import org.jboss.resteasy.test.resource.basic.resource.GenericResourceStudentWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests generic resource class
 * @tpSince RESTEasy 3.0.20
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GenericResourceTest {

   private static GenericResourceStudentInterface proxy;

   @BeforeClass
   public static void setup() {
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(generateURL(""));
      proxy = target.register(GenericResourceStudentReader.class).register(GenericResourceStudentWriter.class).proxy(GenericResourceStudentInterface.class); 
   }

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(GenericResourceTest.class.getSimpleName());
       war.addClass(GenericResourceStudent.class);
       war.addClass(GenericResourceStudentInterface.class);
       war.addClass(GenericResourceCrudResource.class);
       return TestUtil.finishContainerPrepare(war, null, GenericResourceStudentCrudResource.class, GenericResourceStudentReader.class, GenericResourceStudentWriter.class);
   }

   private static String generateURL(String path) {
       return PortProviderUtil.generateURL(path, GenericResourceTest.class.getSimpleName());
   }

   @Test
   public void testGet()
   {
      Assert.assertTrue(proxy.get(1).getName().equals("Jozef Hartinger"));
   }

   @Test
   public void testPut()
   {
      proxy.put(2, new GenericResourceStudent("John Doe"));
      Assert.assertTrue(proxy.get(2).getName().equals("John Doe"));
   }
}
