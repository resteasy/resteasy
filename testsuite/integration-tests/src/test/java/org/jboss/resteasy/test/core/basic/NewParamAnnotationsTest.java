package org.jboss.resteasy.test.core.basic;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceNotAResource;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSomeOtherInterface;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSomeOtherResource;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSuperInt;
import org.jboss.resteasy.test.core.basic.resource.AnnotationInheritanceSuperIntAbstract;
import org.jboss.resteasy.test.core.basic.resource.NewParamAnnotationsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for new param annotations.
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NewParamAnnotationsTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(NewParamAnnotationsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, NewParamAnnotationsResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, NewParamAnnotationsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test new param annotations
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testNewParamAnnotations() {
        Response response = client.target(generateURL("/pathParam0/pathParam1/pathParam2/pathParam3"))
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
}
