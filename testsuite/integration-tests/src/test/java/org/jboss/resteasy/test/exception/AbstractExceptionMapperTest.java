package org.jboss.resteasy.test.exception;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.exception.resource.AbstractMapper;
import org.jboss.resteasy.test.exception.resource.AbstractMapperDefault;
import org.jboss.resteasy.test.exception.resource.AbstractMapperException;
import org.jboss.resteasy.test.exception.resource.AbstractMapperMyCustom;
import org.jboss.resteasy.test.exception.resource.AbstractMapperResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.Type;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-666
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AbstractExceptionMapperTest {

    private Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(AbstractExceptionMapperTest.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        war.addClasses(AbstractMapper.class, AbstractMapperException.class);
        return TestUtil.finishContainerPrepare(war, null, AbstractMapperDefault.class,
                AbstractMapperMyCustom.class, AbstractMapperResource.class);
    }


    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @After
    public void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Correct exception mapper should be chosen when ExceptionMapper implement statement is in abstract class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomUsed() {
        Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(AbstractMapperMyCustom.class, ExceptionMapper.class)[0];
        Assert.assertEquals(AbstractMapperException.class, exceptionType);

        Response response = client.target(PortProviderUtil.generateURL("/resource/custom",
                AbstractExceptionMapperTest.class.getSimpleName())).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("custom", response.readEntity(String.class));
    }
}
