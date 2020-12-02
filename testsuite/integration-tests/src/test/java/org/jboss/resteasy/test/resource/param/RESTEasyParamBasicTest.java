package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import jakarta.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.Params;
import org.jboss.resteasy.test.client.proxy.resource.ProxyBeanParam;
import org.jboss.resteasy.test.client.proxy.resource.ProxyBeanParamResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyParameterAnotations;
import org.jboss.resteasy.test.client.proxy.resource.ProxyParameterAnotationsResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicCustomValuesResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicJaxRsParamDifferentResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicJaxRsParamSameResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicProxy;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicProxyResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicResource;
import org.jboss.resteasy.test.providers.jsonb.basic.JsonBindingTest;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy param annotations (https://issues.jboss.org/browse/RESTEASY-1880)
 *                    Test logic is in the end-point in deployment.
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RESTEasyParamBasicTest {
   protected static final Logger logger = Logger.getLogger(JsonBindingTest.class.getName());

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploySimpleResource() {
      WebArchive war = TestUtil.prepareArchive(RESTEasyParamBasicTest.class.getSimpleName());
      war.addClass(RESTEasyParamBasicProxy.class);
      return TestUtil.finishContainerPrepare(war, null,
            RESTEasyParamBasicResource.class,
            ProxyBeanParamResource.class,
            Params.class,
            ProxyParameterAnotationsResource.class,
            RESTEasyParamBasicJaxRsParamDifferentResource.class,
            RESTEasyParamBasicJaxRsParamSameResource.class,
            RESTEasyParamBasicCustomValuesResource.class,
            RESTEasyParamBasicProxyResource.class);
   }

   @Before
   public void init() {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RESTEasyParamBasicTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails Basic check of new query parameters, matrix parameters, header parameters, cookie parameters and form parameters in proxy clients
    *                Test checks that RESTEasy can inject correct values to method attributes in proxy clients
    *                This test uses new annotations without any annotation value in the proxy, however the annotation values are used in the server side resource.
    * @tpSince RESTEasy 4.0
    */
   @Test
   public void proxyClientAllParamsTest() throws MalformedURLException {
      final String url = generateURL("");
      final String parameterValue = "parameterValue";
      ProxyParameterAnotations proxy = client.target(url).proxy(ProxyParameterAnotations.class);

      String response = proxy.executeQueryParam(parameterValue);
      Assert.assertEquals("QueryParam = "+parameterValue, response);

      response = proxy.executeHeaderParam(parameterValue);
      Assert.assertEquals("HeaderParam = "+parameterValue, response);

      response = proxy.executeCookieParam(parameterValue);
      Assert.assertEquals("CookieParam = "+parameterValue, response);

      response = proxy.executePathParam(parameterValue);
      Assert.assertEquals("PathParam = "+parameterValue, response);

      response = proxy.executeFormParam(parameterValue);
      Assert.assertEquals("FormParam = "+parameterValue, response);

      response = proxy.executeMatrixParam(parameterValue);
      Assert.assertEquals("MatrixParam = "+parameterValue, response);

      //AllAtOnce with RestEasy client
      response = proxy.executeAllParams(
              "queryParam0",
              "headerParam0",
              "cookieParam0",
              "pathParam0",
              "formParam0",
              "matrixParam0");
      Assert.assertEquals("queryParam0 headerParam0 cookieParam0 pathParam0 formParam0 matrixParam0", response);

      //AllAtOnce with MicroProfile client
      ProxyParameterAnotations mpRestClient = RestClientBuilder.newBuilder().baseUrl(new URL(url)).build(ProxyParameterAnotations.class);
      response = mpRestClient.executeAllParams(
              "queryParam0",
              "headerParam0",
              "cookieParam0",
              "pathParam0",
              "formParam0",
              "matrixParam0");
      Assert.assertEquals("queryParam0 headerParam0 cookieParam0 pathParam0 formParam0 matrixParam0", response);
   }

   /**
    * @tpTestDetails Basic check of the Resteasy Microprofile client with BeanParam.
    *                Test checks that Resteasy Microprofile client can inject correct values when BeanParam is used.
    *                This test uses new annotation only without any annotation value.
    * @tpSince RESTEasy 3.7
    */
   @Test
   public void testMicroprofileBeanParam() throws MalformedURLException {
      final String url = generateURL("");
      String response;
      Params params = new Params();
      params.setP1("test");
      params.setP3("param3");
      params.setQ1("queryParam");

     ProxyBeanParam proxy = client.target(url).proxy(ProxyBeanParam.class);
     response = proxy.getAll(params, "param2", "queryParam1");
     Assert.assertEquals("test_param2_param3_queryParam_queryParam1", response);

     ProxyBeanParam mpRestClient = RestClientBuilder.newBuilder().baseUrl(new URL(url)).build(ProxyBeanParam.class);
     response = mpRestClient.getAll(params, "param2", "queryParam1");
     Assert.assertEquals("test_param2_param3_queryParam_queryParam1", response);
   }

   /**
    * @tpTestDetails Basic check of new query parameters, matrix parameters, header parameters, cookie parameters and form parameters
    *                Test checks that RESTEasy can inject correct values to setters, constructors, class variables and method attributes
    *                This test uses new annotation only without any annotation value.
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void basicTest() {
      Response response = client.target(generateURL("/basic/a/pathParam0/pathParam1/pathParam2/pathParam3"))
            .queryParam("queryParam0", "queryParam0")
            .queryParam("queryParam1", "queryParam1")
            .queryParam("queryParam2", "queryParam2")
            .queryParam("queryParam3", "queryParam3")
            .matrixParam("matrixParam0", "matrixParam0")
            .matrixParam("matrixParam1", "matrixParam1")
            .matrixParam("matrixParam2", "matrixParam2")
            .matrixParam("matrixParam3", "matrixParam3")
            .request()
            .header("headerParam0", "headerParam0")
            .header("headerParam1", "headerParam1")
            .header("headerParam2", "headerParam2")
            .header("headerParam3", "headerParam3")
            .cookie("cookieParam0", "cookieParam0")
            .cookie("cookieParam1", "cookieParam1")
            .cookie("cookieParam2", "cookieParam2")
            .cookie("cookieParam3", "cookieParam3")
            .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
            ));
      Assert.assertEquals("Success", 200, response.getStatus());
   }

   /**
    * @tpTestDetails Same check as basicTest with this changes:
    *                  * RESTEasy proxy is used
    *                  * test checks injection to method attributes only
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void proxyTest() {
      Response response = client.target(generateURL("/proxy/a/pathParam3"))
            .queryParam("queryParam3", "queryParam3")
            .matrixParam("matrixParam3", "matrixParam3")
            .request()
            .header("headerParam3", "headerParam3")
            .cookie("cookieParam3", "cookieParam3")
            .post(Entity.form(new Form()
                        .param("formParam3", "formParam3")
            ));
      Assert.assertEquals("Success", 200, response.getStatus());
   }

   /**
    * @tpTestDetails Checks new parameter annotations with custom values
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void customValuesTest() {
      Response response = client.target(generateURL("/custom/values/a/pathParam0/pathParam1/pathParam2/pathParam3"))
            .queryParam("queryParam0", "queryParam0")
            .queryParam("queryParam1", "queryParam1")
            .queryParam("queryParam2", "queryParam2")
            .queryParam("queryParam3", "queryParam3")
            .matrixParam("matrixParam0", "matrixParam0")
            .matrixParam("matrixParam1", "matrixParam1")
            .matrixParam("matrixParam2", "matrixParam2")
            .matrixParam("matrixParam3", "matrixParam3")
            .request()
            .header("headerParam0", "headerParam0")
            .header("headerParam1", "headerParam1")
            .header("headerParam2", "headerParam2")
            .header("headerParam3", "headerParam3")
            .cookie("cookieParam0", "cookieParam0")
            .cookie("cookieParam1", "cookieParam1")
            .cookie("cookieParam2", "cookieParam2")
            .cookie("cookieParam3", "cookieParam3")
            .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
            ));
      Assert.assertEquals("Success", 200, response.getStatus());
   }

   /**
    * @tpTestDetails Checks both original and new parameters together in one end-point, original and new annotations uses the same param names
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void theSameNamesTest() {
      Response response = client.target(generateURL("/same/a/pathParam0/pathParam1/pathParam2/pathParam3"))
            .queryParam("queryParam0", "queryParam0")
            .queryParam("queryParam1", "queryParam1")
            .queryParam("queryParam2", "queryParam2")
            .queryParam("queryParam3", "queryParam3")
            .matrixParam("matrixParam0", "matrixParam0")
            .matrixParam("matrixParam1", "matrixParam1")
            .matrixParam("matrixParam2", "matrixParam2")
            .matrixParam("matrixParam3", "matrixParam3")
            .request()
            .header("headerParam0", "headerParam0")
            .header("headerParam1", "headerParam1")
            .header("headerParam2", "headerParam2")
            .header("headerParam3", "headerParam3")
            .cookie("cookieParam0", "cookieParam0")
            .cookie("cookieParam1", "cookieParam1")
            .cookie("cookieParam2", "cookieParam2")
            .cookie("cookieParam3", "cookieParam3")
            .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
            ));
      Assert.assertEquals("Success", 200, response.getStatus());
   }

   /**
    * @tpTestDetails Checks both original and new parameters together in one end-point, original and new annotations uses different param names
    * @tpSince RESTEasy 3.6
    */
   @Test
   public void differentNamesTest() {
      Response response = client.target(generateURL("/different/a/pathParam0/pathParam1/pathParam2/pathParam3/pathParam4"))
            .queryParam("queryParam0", "queryParam0")
            .queryParam("queryParam1", "queryParam1")
            .queryParam("queryParam2", "queryParam2")
            .queryParam("queryParam3", "queryParam3")
            .queryParam("queryParam4", "queryParam4")
            .matrixParam("matrixParam0", "matrixParam0")
            .matrixParam("matrixParam1", "matrixParam1")
            .matrixParam("matrixParam2", "matrixParam2")
            .matrixParam("matrixParam3", "matrixParam3")
            .matrixParam("matrixParam4", "matrixParam4")
            .request()
            .header("headerParam0", "headerParam0")
            .header("headerParam1", "headerParam1")
            .header("headerParam2", "headerParam2")
            .header("headerParam3", "headerParam3")
            .header("headerParam4", "headerParam4")
            .cookie("cookieParam0", "cookieParam0")
            .cookie("cookieParam1", "cookieParam1")
            .cookie("cookieParam2", "cookieParam2")
            .cookie("cookieParam3", "cookieParam3")
            .cookie("cookieParam4", "cookieParam4")
            .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
                        .param("formParam4", "formParam4")
            ));
      Assert.assertEquals("Success", 200, response.getStatus());
   }

}
