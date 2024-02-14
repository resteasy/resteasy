package org.jboss.resteasy.test.cdi.injection;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.cdi.injection.resource.ApplicationUser;
import org.jboss.resteasy.test.cdi.injection.resource.UserManager;
import org.jboss.resteasy.test.cdi.injection.resource.UserRepository;
import org.jboss.resteasy.test.cdi.injection.resource.UserResource;
import org.jboss.resteasy.test.cdi.injection.resource.UserType;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Injection
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test based on reproducer from WFLY-7037. Enum type UserType is attribute of JPA entity ApplicationUser
 *                    and this attribute is returned via GET request. Note this is not a regression test since in order to
 *                    reproduce the issue
 *                    described in WFLY-7037 is needed to examine the heapdump.
 * @tpSince RESTEasy 3.1.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InjectionJpaEnumTypeTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(InjectionJpaEnumTypeTest.class.getSimpleName());
        war.addClasses(UserManager.class, UserRepository.class, UserResource.class,
                UserType.class, ApplicationUser.class);
        war.addAsResource(InjectionJpaEnumTypeTest.class.getPackage(), "injectionJpaEnumType/persistence.xml",
                "META-INF/persistence.xml");
        war.addAsResource(InjectionJpaEnumTypeTest.class.getPackage(), "injectionJpaEnumType/create.sql",
                "META-INF/create.sql");
        war.addAsResource(InjectionJpaEnumTypeTest.class.getPackage(), "injectionJpaEnumType/load.sql", "META-INF/load.sql");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InjectionJpaEnumTypeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Retrieves attribute UserType from the datasource in json format
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testEnumJackson() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/user"));
        String val = base.request().accept(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class);
        Assertions.assertEquals("{\"id\":1,\"userType\":\"TYPE_ONE\"}", val);
        client.close();
    }

    /**
     * @tpTestDetails Retrieves attribute UserType from the datasource in xml format
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testEnumJaxb() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/user"));
        String val = base.request().accept(MediaType.APPLICATION_XML_TYPE).get().readEntity(String.class);
        Assertions.assertTrue(val.contains("<applicationUser><id>1</id><userType>TYPE_ONE</userType></applicationUser>"));
        client.close();
    }
}
