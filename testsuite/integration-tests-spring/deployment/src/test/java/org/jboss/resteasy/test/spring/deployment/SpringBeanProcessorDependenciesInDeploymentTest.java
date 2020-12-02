package org.jboss.resteasy.test.spring.deployment;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorCounter;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorCustomer;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorCustomerParamConverter;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorCustomerParamConverterProvider;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorCustomerService;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyBean;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyBeanFactoryBean;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyInnerBean;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyIntercepted;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyInterceptedResource;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyInterceptor;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyPrototypedResource;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyResource;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorMyWriter;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorResourceConfiguration;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorScannedResource;
import org.jboss.resteasy.test.spring.deployment.resource.SpringBeanProcessorSpringBeanProcessorMyInnerBeanImpl;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests - dependencies included in deployment
 * @tpTestCaseDetails This class tests a gamut of Spring related functionality including @Configuration beans, @Autowired,
 * scanned beans, interceptors and overall integration between RESTEasy and the Spring ApplicationContext.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SpringBeanProcessorDependenciesInDeploymentTest {

   static Client client;
   private static final String ERROR_MESSAGE = "Got unexpected entity from the server";

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, SpringBeanProcessorDependenciesInDeploymentTest.class.getSimpleName());
   }

   @Before
   public void init() {
      client = ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   @Deployment
   private static Archive<?> deploy() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, SpringBeanProcessorDependenciesInDeploymentTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(SpringBeanProcessorDependenciesInDeploymentTest.class.getPackage(), "web.xml", "web.xml");
      archive.addAsWebInfResource(SpringBeanProcessorDependenciesInDeploymentTest.class.getPackage(),
            "springBeanProcessor/spring-bean-processor-test.xml", "applicationContext.xml");
      archive.addClass(SpringBeanProcessorCounter.class);
      archive.addClass(SpringBeanProcessorCustomer.class);
      archive.addClass(SpringBeanProcessorCustomerService.class);
      archive.addClass(SpringBeanProcessorMyBean.class);
      archive.addClass(SpringBeanProcessorMyBeanFactoryBean.class);
      archive.addClass(SpringBeanProcessorMyInnerBean.class);
      archive.addClass(SpringBeanProcessorSpringBeanProcessorMyInnerBeanImpl.class);
      archive.addClass(SpringBeanProcessorMyIntercepted.class);
      archive.addClass(SpringBeanProcessorMyInterceptedResource.class);
      archive.addClass(SpringBeanProcessorMyInterceptor.class);
      archive.addClass(SpringBeanProcessorMyPrototypedResource.class);
      archive.addClass(SpringBeanProcessorMyResource.class);
      archive.addClass(SpringBeanProcessorMyWriter.class);
      archive.addClass(SpringBeanProcessorResourceConfiguration.class);
      archive.addClass(SpringBeanProcessorCustomerParamConverter.class);
      archive.addClass(SpringBeanProcessorCustomerParamConverterProvider.class);
      archive.addClass(SpringBeanProcessorScannedResource.class);

      // Permission needed for "arquillian.debug" to run
      // "suppressAccessChecks" required for access to arquillian-core.jar
      // remaining permissions needed to run springframework
      archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("org.springframework.cglib.test.stressHashCodes", "read"),
            new PropertyPermission("arquillian.*", "read"),
            new PropertyPermission("cglib.debugLocation", "read"),
            new RuntimePermission("getClassLoader"),
            new RuntimePermission("getProtectionDomain"),
            new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers"),
            new FilePermission("<<ALL FILES>>", "read"),
            new LoggingPermission("control", "")
      ), "permissions.xml");

      TestUtilSpring.addSpringLibraries(archive);
      TestUtil.addOtherLibrary(archive, "aopalliance:aopalliance:" + System.getProperty("version.aopalliance", "1.0"));
      return archive;
   }

   /**
    * @tpTestDetails Tests org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator integration with Resteasy
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAutoProxy() throws Exception {
      WebTarget target = client.target(generateURL("/intercepted"));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "springBeanProcessorCustomer=bill", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Tests that resource bean defined in xml spring application context is registred by resourceBeanProcessor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testProcessor() throws Exception {
      WebTarget target = client.target(generateURL(""));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "springBeanProcessorCustomer=bill", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Tests that resource bean defined in xml spring application context with scope prototype
    * is registred by resourceBeanProcessor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testPrototyped() throws Exception {
      WebTarget target = client.target(generateURL("/prototyped/1"));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "bill0", response.readEntity(String.class));

      response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "bill0", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Tests that resource is automatically registered without defining it in spring application context
    * configuration file, but defined programatically with @Configuration annotation
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testRegistration() throws Exception {
      WebTarget target = client.target(generateURL("/registered/singleton/count"));
      Response response = target.request().post(null);
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "0", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Tests that resource is automatically registered without defining it in spring application context
    * configuration file
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testScanned() throws Exception {
      WebTarget target = client.target(generateURL("/scanned"));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "Hello", response.readEntity(String.class));
   }

   /**
    * @tpTestDetails Tests that resource is available when using @Autowired annotation for the service
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testAutowiredProvider() throws Exception {
      WebTarget target = client.target(generateURL("/customer-name?name=Solomon"));
      Response response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "springBeanProcessorCustomer=Solomon", response.readEntity(String.class));

      target = client.target(generateURL("/customer-object?customer=Solomon"));
      response = target.request().get();
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
      Assert.assertEquals(ERROR_MESSAGE, "Solomon", response.readEntity(String.class));
   }
}
