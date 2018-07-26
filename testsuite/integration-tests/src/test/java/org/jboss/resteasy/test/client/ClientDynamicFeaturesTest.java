package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesClientFeature1;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesClientFeature2;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesDualFeature1;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesDualFeature2;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesServerFeature1;
import org.jboss.resteasy.test.client.resource.ClientDynamicFeaturesServerFeature2;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.container.DynamicFeature;
import java.lang.reflect.ReflectPermission;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.util.Set;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1083
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class ClientDynamicFeaturesTest {
    private static final String CLIENT_FEATURE_ERROR_MSG = "Wrong count of client features";
    private static final String SERVER_FEATURE_ERROR_MSG = "Wrong count of server features";

    /**
     * Test needs to be run on deployment.
     * @return
     */
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientDynamicFeaturesTest.class.getSimpleName());
        war.addClasses(ClientDynamicFeaturesClientFeature1.class,
                ClientDynamicFeaturesClientFeature2.class,
                ClientDynamicFeaturesDualFeature2.class,
                ClientDynamicFeaturesDualFeature1.class,
                ClientDynamicFeaturesServerFeature2.class,
                ClientDynamicFeaturesServerFeature1.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("arquillian.*", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Check dynamic feature counts.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDynamicFeatures() throws Exception {
        ResteasyProviderFactory factory =  ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);

        factory.registerProvider(ClientDynamicFeaturesClientFeature1.class, 0, false, null);
        factory.registerProvider(ClientDynamicFeaturesServerFeature1.class, 0, false, null);
        factory.registerProvider(ClientDynamicFeaturesDualFeature1.class, 0, false, null);
        ClientDynamicFeaturesClientFeature2 clientFeature = new ClientDynamicFeaturesClientFeature2();
        ClientDynamicFeaturesServerFeature2 serverFeature = new ClientDynamicFeaturesServerFeature2();
        ClientDynamicFeaturesDualFeature2 feature = new ClientDynamicFeaturesDualFeature2();
        factory.registerProviderInstance(clientFeature, null, 0, false);
        factory.registerProviderInstance(serverFeature, null, 0, false);
        factory.registerProviderInstance(feature, null, 0, false);
        Set<DynamicFeature> clientFeatureSet = factory.getClientDynamicFeatures();
        Set<DynamicFeature> serverFeatureSet = factory.getServerDynamicFeatures();

        Assert.assertEquals(CLIENT_FEATURE_ERROR_MSG, 1, countFeatures(clientFeatureSet, "ClientDynamicFeaturesClientFeature1"));
        Assert.assertEquals(CLIENT_FEATURE_ERROR_MSG, 1, countFeatures(clientFeatureSet, "ClientDynamicFeaturesClientFeature2"));
        Assert.assertEquals(CLIENT_FEATURE_ERROR_MSG, 1, countFeatures(clientFeatureSet, "ClientDynamicFeaturesDualFeature1"));
        Assert.assertEquals(CLIENT_FEATURE_ERROR_MSG, 1, countFeatures(clientFeatureSet, "ClientDynamicFeaturesDualFeature2"));
        Assert.assertEquals(SERVER_FEATURE_ERROR_MSG, 1, countFeatures(serverFeatureSet, "ClientDynamicFeaturesServerFeature1"));
        Assert.assertEquals(SERVER_FEATURE_ERROR_MSG, 1, countFeatures(serverFeatureSet, "ClientDynamicFeaturesServerFeature2"));
        Assert.assertEquals(SERVER_FEATURE_ERROR_MSG, 1, countFeatures(serverFeatureSet, "ClientDynamicFeaturesDualFeature1"));
        Assert.assertEquals(SERVER_FEATURE_ERROR_MSG, 1, countFeatures(serverFeatureSet, "ClientDynamicFeaturesDualFeature2"));
    }

    private int countFeatures(Set<DynamicFeature> featureSet, String feature) {
        int count = 0;
        for (Iterator<DynamicFeature> it = featureSet.iterator(); it.hasNext(); ) {
            Class<?> clazz = it.next().getClass();
            if (clazz.getName().contains(feature)) {
                count++;
            }
        }
        return count;
    }
}
