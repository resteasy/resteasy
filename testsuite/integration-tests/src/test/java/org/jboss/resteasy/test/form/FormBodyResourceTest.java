package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.FormBodyResourceClient;
import org.jboss.resteasy.test.form.resource.FormBodyResourceForm;
import org.jboss.resteasy.test.form.resource.FormBodyResourceResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormBodyResourceTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormParameterTest.class.getSimpleName());
        war.addClasses(FormBodyResourceClient.class);
        war.addClasses(FormBodyResourceForm.class);
        return TestUtil.finishContainerPrepare(war, null, FormBodyResourceResource.class);
    }

    /**
     * @tpTestDetails Check body of form.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        FormBodyResourceClient proxy = client.target(
                PortProviderUtil.generateBaseUrl(FormParameterTest.class.getSimpleName()))
                .proxyBuilder(FormBodyResourceClient.class).build();
        Assert.assertEquals("foo.gotIt", proxy.put("foo"));
        client.close();
    }
}
