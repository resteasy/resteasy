package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterCompany;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterCompanyConverter;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterMyClient;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterObjectConverter;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterPerson;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterPersonConverter;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterResource;
import org.jboss.resteasy.test.resource.param.resource.SuperStringConverterSuperPersonConverter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for org.jboss.resteasy.spi.StringConverter class
 *                    StringConverter is deprecated.
 *                    See javax.ws.rs.ext.ParamConverter
 *                    See org.jboss.resteasy.test.resource.param.ParamConverterTest
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SuperStringConverterTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SuperStringConverterTest.class.getSimpleName());
        war.addClass(SuperStringConverterPerson.class);
        war.addClass(SuperStringConverterObjectConverter.class);
        war.addClass(SuperStringConverterSuperPersonConverter.class);
        war.addClass(SuperStringConverterMyClient.class);
        war.addClass(SuperStringConverterCompany.class);
        return TestUtil.finishContainerPrepare(war, null, SuperStringConverterPersonConverter.class,
                SuperStringConverterCompanyConverter.class,
                SuperStringConverterResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(SuperStringConverterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test converter on basic object
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPerson() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(SuperStringConverterPersonConverter.class);
        client.register(SuperStringConverterCompanyConverter.class);

        SuperStringConverterMyClient proxy = ProxyBuilder.builder(SuperStringConverterMyClient.class, client.target(generateBaseUrl())).build();
        SuperStringConverterPerson person = new SuperStringConverterPerson("name");
        proxy.put(person);
        client.close();
    }

    /**
     * @tpTestDetails Test converter on object with override on "toString" method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCompany() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(SuperStringConverterPersonConverter.class);
        client.register(SuperStringConverterCompanyConverter.class);
        SuperStringConverterMyClient proxy = ProxyBuilder.builder(SuperStringConverterMyClient.class, client.target(generateBaseUrl())).build();
        SuperStringConverterCompany company = new SuperStringConverterCompany("name");
        proxy.putCompany(company);
        client.close();
    }
}
