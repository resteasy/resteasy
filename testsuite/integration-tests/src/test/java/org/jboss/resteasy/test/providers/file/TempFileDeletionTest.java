package org.jboss.resteasy.test.providers.file;

import java.io.File;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.providers.file.resource.TempFileDeletionResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.experimental.categories.Category;

/**
 * @tpSubChapter File provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.23.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TempFileDeletionTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TempFileDeletionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TempFileDeletionResource.class);
    }

    /**
     * @tpTestDetails Resource method contains parameter of the type File. This triggers File provider, which creates
     * temporary file on the server side, which is automatically deleted in the end of the resource method invocation.
     * @tpInfo Regression test for RESTEASY-1464
     * @tpSince RESTEasy 3.0.23.Final
     */
    @Test
    @Category(NotForForwardCompatibility.class)
    public void testDeleteOnServer() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(PortProviderUtil.generateURL("/test/post", TempFileDeletionTest.class.getSimpleName()));
        Response response = base.request().post(Entity.entity("hello", "text/plain"));
        Assert.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
        String path = response.readEntity(String.class);
        File file = new File(path);
        Assert.assertFalse(file.exists());
        client.close();
    }
}
