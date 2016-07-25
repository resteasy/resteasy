package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.CRUDEntityWebservice;
import org.jboss.resteasy.test.providers.jettison.resource.MyService;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingResource;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingTestBase;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingTestExtends;
import org.jboss.resteasy.test.providers.jettison.resource.ObjectFactory;
import org.jboss.resteasy.test.providers.jettison.resource.UserEntity;
import org.jboss.resteasy.test.providers.jettison.resource.UserEntityWebservice;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-213
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyInheritanceTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyInheritanceTest.class.getSimpleName());
        war.addClasses(NamespaceMappingTestBase.class, NamespaceMappingTestExtends.class,
                NamespaceMappingResource.class, ObjectFactory.class);
        war.addClasses(CRUDEntityWebservice.class, MyService.class,
                UserEntity.class, UserEntityWebservice.class);
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, MyService.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ProxyInheritanceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewClient() {
        UserEntity u = new UserEntity();
        u.setUsername("user");

        UserEntityWebservice serviceClient =  client.target(generateBaseUrl()).proxy(UserEntityWebservice.class);
        UserEntity newUser = serviceClient.create(u);

        assertEquals("Wrong response from proxy", "user", newUser.getUsername());
    }
}
