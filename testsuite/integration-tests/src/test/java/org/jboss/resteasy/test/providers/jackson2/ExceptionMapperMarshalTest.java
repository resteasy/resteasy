package org.jboss.resteasy.test.providers.jackson2;

import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperIOExceptionMapper;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperMarshalErrorMessage;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperMarshalMyCustomException;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperMarshalMyCustomExceptionMapper;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperMarshalName;
import org.jboss.resteasy.test.providers.jackson2.resource.ExceptionMapperMarshalResource;
import org.jboss.resteasy.test.providers.jackson2.resource.MyEntity;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-937
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExceptionMapperMarshalTest {

    protected static final Logger logger = Logger.getLogger(ProxyWithGenericReturnTypeJacksonTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
        war.addClass(Jackson2Test.class);
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new RuntimePermission("getProtectionDomain"),
                new ReflectPermission("suppressAccessChecks"),
                new PropertyPermission("resteasy.server.tracing.*", "read")), "permissions.xml");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        return TestUtil.finishContainerPrepare(war, contextParam, ExceptionMapperMarshalErrorMessage.class,
                ExceptionMapperMarshalMyCustomException.class,
                MyEntity.class, ExceptionMapperIOExceptionMapper.class,
                ExceptionMapperMarshalMyCustomExceptionMapper.class, ExceptionMapperMarshalName.class,
                ExceptionMapperMarshalResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests usage of custom ExceptionMapper producing json response
     * @tpPassCrit The resource returns Success response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomUsed() {
        Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ExceptionMapperMarshalMyCustomExceptionMapper.class,
                ExceptionMapper.class)[0];
        Assertions.assertEquals(ExceptionMapperMarshalMyCustomException.class, exceptionType);

        Response response = client.target(generateURL("/resource/custom")).request().get();
        Assertions.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
        List<ExceptionMapperMarshalErrorMessage> errors = response
                .readEntity(new GenericType<List<ExceptionMapperMarshalErrorMessage>>() {
                });
        Assertions.assertEquals("error", errors.get(0).getError(),
                "The response has unexpected content");
    }

    @Test
    public void testMyCustomUsed() {
        Response response = client.target(generateURL("/resource/customME")).request().get();
        String text = response.readEntity(String.class);

        Assertions.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
        Assertions.assertTrue(text.contains("UN_KNOWN_ERR"), "Response does not contain UN_KNOWN_ERR");
    }
}
