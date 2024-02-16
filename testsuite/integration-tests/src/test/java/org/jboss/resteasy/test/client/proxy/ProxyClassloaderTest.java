package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.proxy.resource.ClassloaderResource;
import org.jboss.resteasy.test.client.proxy.resource.ClientSmokeResource;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyClassloaderTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ProxyClassloaderTest.class.getSimpleName());
        war.addClass(ResourceWithInterfaceSimpleClient.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new RuntimePermission("setContextClassLoader"),
                new RuntimePermission("createClassLoader"),
                new RuntimePermission("getClassLoader")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ClientSmokeResource.class, ClassloaderResource.class);
    }

    @Test
    public void testNoTCCL() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        String target2 = PortProviderUtil.generateURL("", ProxyClassloaderTest.class.getSimpleName());
        String target = PortProviderUtil.generateURL("/cl/cl?param=" + target2, ProxyClassloaderTest.class.getSimpleName());
        Response response = client.target(target).request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals("basic", entity);
        response.close();
        client.close();
    }

}
