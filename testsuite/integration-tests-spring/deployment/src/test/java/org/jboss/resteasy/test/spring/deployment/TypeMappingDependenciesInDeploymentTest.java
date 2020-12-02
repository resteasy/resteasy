package org.jboss.resteasy.test.spring.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.spring.deployment.resource.TypeMappingResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests - dependencies included in deployment
 * @tpTestCaseDetails Test extension mapping by ResteasyDeployment property mediaTypeMappings.
 * Logic of this test is in spring-typemapping-test-server.xml.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TypeMappingDependenciesInDeploymentTest {


   @Deployment
   private static Archive<?> deploy() {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, TypeMappingDependenciesInDeploymentTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(TypeMappingDependenciesInDeploymentTest.class.getPackage(), "web.xml", "web.xml");
      archive.addAsWebInfResource(TypeMappingDependenciesInDeploymentTest.class.getPackage(), "typeMapping/spring-typemapping-test-server.xml", "applicationContext.xml");
      archive.addClass(TypeMappingResource.class);

      archive.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
              new PropertyPermission("org.graalvm.nativeimage.imagecode", "read"),
              new RuntimePermission("getenv.RESTEASY_SERVER_TRACING_THRESHOLD"),
              new RuntimePermission("getenv.resteasy_server_tracing_threshold"),
              new RuntimePermission("getenv.resteasy.server.tracing.threshold"),
              new RuntimePermission("getenv.RESTEASY_SERVER_TRACING_TYPE"),
              new RuntimePermission("getenv.resteasy_server_tracing_type"),
            new RuntimePermission("getenv.resteasy.server.tracing.type"),
            new ReflectPermission("suppressAccessChecks"),
            new RuntimePermission("accessDeclaredMembers"),
            new RuntimePermission("getClassLoader"),
            new RuntimePermission("getenv.resteasy.server.tracing.type"),
            new FilePermission("<<ALL FILES>>", "read"),
            new LoggingPermission("control", "")
      ), "permissions.xml");

      TestUtilSpring.addSpringLibraries(archive);
      return archive;
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, TypeMappingDependenciesInDeploymentTest.class.getSimpleName());
   }

   private void requestAndAssert(String path, String extension, String accept, String expectedContentType) {
      // prepare URL
      String url = generateURL("/test/" + path);
      if (extension != null) {
         url = url + "." + extension;
      }

      // make request
      Client client = ResteasyClientBuilder.newClient();
      WebTarget target = client.target(url);
      Invocation.Builder builder = target.request();
      if (accept != null) {
         builder.accept(accept);
      }
      Response response = builder.get();
      int status = response.getStatus();
      String contentType = response.getHeaderString("Content-type");
      assertEquals("Request for " + url + " returned a non-200 status", HttpResponseCodes.SC_OK, status);
      assertEquals("Request for " + url + " returned an unexpected content type", expectedContentType, contentType);

      // close
      response.close();
      client.close();
   }

   /**
    * @tpTestDetails Test various option of type mapping. Reproducer for RESTEASY-1287.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void test() throws Exception {
      // acceptJSONAndXMLRequestNoProducesJSONExtension() throws Exception {
      requestAndAssert("noproduces", "json", "application/json, application/xml", "application/json");

      // acceptJSONAndXMLRequestNoProducesXMLExtension() throws Exception {
      requestAndAssert("noproduces", "xml", "application/json, application/xml", "application/xml;charset=UTF-8");

      // acceptJSONOnlyRequestNoProducesJSONExtension() throws Exception {
      requestAndAssert("noproduces", "json", "application/json", "application/json");

      // acceptJSONOnlyRequestNoProducesNoExtension() throws Exception {
      requestAndAssert("noproduces", null, "application/json", "application/json");

      // acceptJSONOnlyRequestNoProducesXMLExtension() throws Exception {
      requestAndAssert("noproduces", "xml", "application/json", "application/xml;charset=UTF-8");

      // acceptNullRequestNoProducesJSONExtension() throws Exception {
      requestAndAssert("noproduces", "json", null, "application/json");

      // acceptNullRequestNoProducesXMLExtension() throws Exception {
      requestAndAssert("noproduces", "xml", null, "application/xml;charset=UTF-8");

      // acceptXMLAndJSONRequestNoProducesJSONExtension() throws Exception {
      requestAndAssert("noproduces", "json", "application/xml, application/json", "application/json");

      // acceptXMLAndJSONRequestNoProducesXMLExtension() throws Exception {
      requestAndAssert("noproduces", "xml", "application/xml, application/json", "application/xml;charset=UTF-8");

      // acceptXMLOnlyRequestNoProducesJSONExtension() throws Exception {
      requestAndAssert("noproduces", "json", "application/xml", "application/json");

      // acceptXMLOnlyRequestNoProducesNoExtension() throws Exception {
      requestAndAssert("noproduces", null, "application/xml", "application/xml;charset=UTF-8");

      // acceptXMLOnlyRequestNoProducesXMLExtension() throws Exception {
      requestAndAssert("noproduces", "xml", "application/xml", "application/xml;charset=UTF-8");
   }
}
