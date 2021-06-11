package org.jboss.resteasy.test.cdi.stereotype;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.Dummy;
import org.jboss.resteasy.test.cdi.stereotype.resource.DummyProviderResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.InvalidProviderStereotypeResource;
import org.jboss.resteasy.test.cdi.stereotype.resource.TestApplication;
import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.ProviderStereotype;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests whether deployment fails since @Provider stereotype cannot be used as a method annotation.
 * @tpSince RESTEasy 4.7.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InvalidStereotypeWithProviderOnMethodTest {

    @ArquillianResource
    private Deployer deployer;

    private static Client client;

    @BeforeClass
    public static void setup()
    {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    @Deployment(name = "deployment", managed = false)
    private static Archive<?> deploy(){
        WebArchive war = TestUtil.prepareArchive(InvalidStereotypeWithProviderOnMethodTest.class.getSimpleName());
        war.addClasses(TestApplication.class, ProviderStereotype.class, Dummy.class, DummyProviderResource.class, InvalidProviderStereotypeResource.class);
        return war;
    }

    @Test
    public void testProviderStereotype(){
        try{
            deployer.deploy("deployment");
            Assert.fail("DeploymentException was not thrown as expected");
        }
        catch (Exception deploymentException){
            String message = deploymentException.getMessage();
            Assert.assertTrue("Expected error was not a cause for deployment failure", message.contains("ProviderStereotype"));
            Assert.assertTrue("Expected error was not a cause for deployment failure", message.contains("RESTEASY010635"));
        }
    }
}
