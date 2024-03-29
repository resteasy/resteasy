package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.proxy.resource.ContentLanguageInterface;
import org.jboss.resteasy.test.client.proxy.resource.ContentLanguageResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
//Test for RESTEASY-1537:Client proxy framework clears previously set Content-Language header when setting POST message body entity
public class ContentLanguageProxyTest {
    private static ResteasyClient client;

    @BeforeAll
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ContentLanguageProxyTest.class.getSimpleName());
        war.addClasses(ContentLanguageInterface.class);
        return TestUtil.finishContainerPrepare(war, null, ContentLanguageResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ContentLanguageProxyTest.class.getSimpleName());
    }

    @Test
    public void testProxy() throws Exception {
        ResteasyWebTarget target = client.target(generateBaseUrl());
        ContentLanguageInterface proxy = target.proxy(ContentLanguageInterface.class);
        String contentLangFirst = proxy.contentLang1("fr", "subject");
        Assertions.assertEquals("frsubject", contentLangFirst,
                "Content_Language header is expected set in request");
        String contentLangSecond = proxy.contentLang2("subject", "fr");
        Assertions.assertEquals("subjectfr", contentLangSecond,
                "Content_Language header is expected set in request");
    }
}
