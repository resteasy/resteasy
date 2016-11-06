package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.delegates.DateDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateDate;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateDelegate;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateInterface1;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateInterface2;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateInterface3;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateInterface4;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateResource;
import org.jboss.resteasy.test.resource.param.resource.HeaderDelegateSubDelegate;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-915
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class HeaderDelegateTest {
    private static Logger logger = Logger.getLogger(HeaderDelegateTest.class);

    public static final Date RIGHT_AFTER_BIG_BANG = new HeaderDelegateDate(3000);


    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(HeaderDelegateTest.class.getSimpleName());
        war.addClass(HeaderDelegateDate.class);
        war.addClass(HeaderDelegateDelegate.class);
        war.addClass(HeaderDelegateInterface1.class);
        war.addClass(HeaderDelegateInterface2.class);
        war.addClass(HeaderDelegateInterface3.class);
        war.addClass(HeaderDelegateInterface4.class);
        war.addClass(HeaderDelegateSubDelegate.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(HeaderDelegateTest.class);
        return TestUtil.finishContainerPrepare(war, null, HeaderDelegateResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HeaderDelegateTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test delegation by client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void lastModifiedTest() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/last"));
        Invocation.Builder request = target.request();
        Response response = request.get();
        logger.info("lastModified string: " + response.getHeaderString("last-modified"));
        Date last = response.getLastModified();
        Assert.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
        Assert.assertEquals("Wrong response", DateUtil.formatDate(RIGHT_AFTER_BIG_BANG), DateUtil.formatDate(last));
        client.close();
    }

    /**
     * @tpTestDetails Check delegation rules from ResteasyProviderFactory
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void localTest() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        Assert.assertEquals("Wrong delegation", DateDelegate.class, factory.getHeaderDelegate(HeaderDelegateDate.class).getClass());
        Assert.assertEquals("Wrong delegation", DateDelegate.class, factory.createHeaderDelegate(HeaderDelegateDate.class).getClass());

        @SuppressWarnings("rawtypes")
        HeaderDelegateSubDelegate<?> delegate = new HeaderDelegateSubDelegate();
        factory.addHeaderDelegate(HeaderDelegateInterface1.class, delegate);
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateInterface1.class));
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateInterface2.class));
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateInterface3.class));
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateInterface4.class));
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateDelegate.class));
        Assert.assertEquals("Wrong delegation", delegate, factory.getHeaderDelegate(HeaderDelegateSubDelegate.class));
    }
}
