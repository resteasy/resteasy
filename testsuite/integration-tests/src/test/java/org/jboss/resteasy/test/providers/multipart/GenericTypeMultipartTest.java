package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.test.providers.multipart.resource.GenericTypeResource;
import org.jboss.resteasy.test.providers.multipart.resource.GenericTypeStringListReaderWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-1795
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GenericTypeMultipartTest {
    public static final GenericType<List<String>> stringListType = new GenericType<List<String>>() {
    };


    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GenericTypeMultipartTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, GenericTypeResource.class, GenericTypeStringListReaderWriter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GenericTypeMultipartTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails List is in request.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGenericType() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().register(GenericTypeStringListReaderWriter.class).build();
        ResteasyWebTarget target = client.target(generateURL("/test"));
        MultipartFormDataOutput output = new MultipartFormDataOutput();
        List<String> list = new ArrayList<>();
        list.add("darth");
        list.add("sidious");
        output.addFormData("key", list, stringListType, MediaType.APPLICATION_XML_TYPE);
        Entity<MultipartFormDataOutput> entity = Entity.entity(output, MediaType.MULTIPART_FORM_DATA_TYPE);
        String response = target.request().post(entity, String.class);
        Assert.assertEquals("Wrong response content", "darth sidious ", response);
        client.close();
    }

}
