package org.jboss.resteasy.test.spring.deployment;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import javax.management.MBeanPermission;
import javax.management.MBeanServerPermission;
import javax.management.MBeanTrustPermission;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.ClientURI;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.spring.deployment.resource.Contact;
import org.jboss.resteasy.test.spring.deployment.resource.ContactService;
import org.jboss.resteasy.test.spring.deployment.resource.Contacts;
import org.jboss.resteasy.test.spring.deployment.resource.ContactsResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests - dependencies included in deployment
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContactsDependenciesInDeploymentTest {

   private static Logger logger = Logger.getLogger(ContactsDependenciesInDeploymentTest.class);
   private static ContactProxy proxy;
   private static ResteasyClient client;


   @Path(ContactsResource.CONTACTS_URL)
   public interface ContactProxy {
      @Path("data")
      @POST
      @Consumes(MediaType.APPLICATION_XML)
      Response createContact(Contact contact);

      @GET
      @Produces(MediaType.APPLICATION_XML)
      Contact getContact(@ClientURI String uri);

      @GET
      String getString(@ClientURI String uri);
   }

   @Deployment
   private static Archive<?> deploy() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, ContactsDependenciesInDeploymentTest.class.getSimpleName() + ".war")
            .addClass(ContactsResource.class)
            .addClass(ContactService.class)
            .addClass(Contacts.class)
            .addClass(Contact.class)
            .addClass(ContactsDependenciesInDeploymentTest.class)
            .addAsWebInfResource(ContactsDependenciesInDeploymentTest.class.getPackage(), "contacts/web.xml", "web.xml")
            .addAsWebInfResource(ContactsDependenciesInDeploymentTest.class.getPackage(), "contacts/springmvc-servlet.xml", "springmvc-servlet.xml");

      // spring specific permissions needed.
      // Permission  accessClassInPackage.sun.reflect.annotation is required in order
      // for spring to introspect annotations.  Security exception is eaten by spring
      // and not posted via the server.
      archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("org.graalvm.nativeimage.imagecode", "read"),
              new RuntimePermission("getenv.RESTEASY_SERVER_TRACING_THRESHOLD"),
              new RuntimePermission("getenv.resteasy_server_tracing_threshold"),
              new RuntimePermission("getenv.resteasy.server.tracing.threshold"),
              new RuntimePermission("getenv.RESTEASY_SERVER_TRACING_TYPE"),
              new RuntimePermission("getenv.resteasy_server_tracing_type"),
              new RuntimePermission("getenv.resteasy.server.tracing.type"),
            new MBeanServerPermission("createMBeanServer"),
            new MBeanPermission("org.springframework.context.support.LiveBeansView#-[liveBeansView:application=/ContactsDependenciesInDeploymentTest]", "registerMBean,unregisterMBean"),
            new MBeanTrustPermission("register"),
            new PropertyPermission("spring.liveBeansView.mbeanDomain", "read"),
              new RuntimePermission("getClassLoader"),
            new RuntimePermission("getenv.spring.liveBeansView.mbeanDomain"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers"),
            new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
            new FilePermission("<<ALL FILES>>", "read"),
            new LoggingPermission("control", "")
      ), "permissions.xml");

      TestUtilSpring.addSpringLibraries(archive);
      return archive;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, ContactsDependenciesInDeploymentTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Test is using component-scan and annotation-config spring features. This features are unusable if
    * running with spring dependency 3.2.8.RELEASE and earlier. Only Spring 4 is supported.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testData() {
      client = (ResteasyClient) ClientBuilder.newClient();
      proxy = client.target(generateURL("")).proxy(ContactProxy.class);
      Response response = proxy.createContact(new Contact("Solomon", "Duskis"));
      Assert.assertEquals(201, response.getStatus());
      String duskisUri = (String) response.getMetadata().getFirst(HttpHeaderNames.LOCATION);
      logger.info(duskisUri);
      Assert.assertTrue("Unexpected response from the server", duskisUri.endsWith(ContactsResource.CONTACTS_URL + "/data/Duskis"));
      response.close();
      Assert.assertEquals("Unexpected response from the server", "Solomon", proxy.getContact(duskisUri).getFirstName());
      response = proxy.createContact(new Contact("Bill", "Burkie"));
      response.close();
      logger.info(proxy.getString(generateURL(ContactsResource.CONTACTS_URL + "/data")));
   }
}
