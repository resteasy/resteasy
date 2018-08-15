package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceAbstractBackendCollectionResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceAbstractBackendResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceAbstractBackendSubResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceAction;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceAssignedPermissionsResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBackendDataCenterResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBackendDataCentersResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBackendResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBaseBackendResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBaseResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBaseResources;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceBusinessEntity;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceDataCenter;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceDataCenterResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceDataCenters;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceDataCentersResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceGuid;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceINotifyPropertyChanged;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceIVdcQueryable;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceTop;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceUpdatableResource;
import org.jboss.resteasy.test.providers.jaxb.resource.GenericSuperInterfaceStoragePool;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-636
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class GenericSuperInterfaceTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GenericSuperInterfaceTest.class.getSimpleName());
        war.addClasses(GenericSuperInterfaceBackendDataCentersResource.class,
                GenericSuperInterfaceAbstractBackendCollectionResource.class,
                GenericSuperInterfaceAbstractBackendResource.class,
                GenericSuperInterfaceAbstractBackendSubResource.class,
                GenericSuperInterfaceAction.class, GenericSuperInterfaceAssignedPermissionsResource.class,
                GenericSuperInterfaceBackendDataCenterResource.class,
                GenericSuperInterfaceBackendDataCentersResource.class,
                GenericSuperInterfaceBackendResource.class,
                GenericSuperInterfaceBaseResource.class, GenericSuperInterfaceBaseResources.class,
                GenericSuperInterfaceBusinessEntity.class,
                GenericSuperInterfaceDataCenter.class, GenericSuperInterfaceDataCenterResource.class,
                GenericSuperInterfaceDataCenters.class,
                GenericSuperInterfaceDataCentersResource.class, GenericSuperInterfaceGuid.class,
                GenericSuperInterfaceINotifyPropertyChanged.class, GenericSuperInterfaceIVdcQueryable.class,
                GenericSuperInterfaceStoragePool.class, GenericSuperInterfaceUpdatableResource.class,
                GenericSuperInterfaceBaseBackendResource.class,
                TestUtil.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("arquillian.*", "read")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, GenericSuperInterfaceTop.class);
    }

    /**
     * @tpTestDetails Test on server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetImplementationReflection() throws Exception {
        Class updatableResource = GenericSuperInterfaceBackendDataCenterResource.class.getInterfaces()[0].getInterfaces()[0];
        Assert.assertEquals(updatableResource, GenericSuperInterfaceUpdatableResource.class);
        Method update = null;
        for (Method method : updatableResource.getMethods()) {
            if (method.getName().equals("update")) {
                update = method;
            }
        }
        Assert.assertNotNull("Updated method was not found", update);

        Method implemented = Types.getImplementingMethod(GenericSuperInterfaceBackendDataCenterResource.class, update);

        Method actual = null;
        for (Method method : GenericSuperInterfaceBackendDataCenterResource.class.getMethods()) {
            if (method.getName().equals("update") && !method.isSynthetic()) {
                actual = method;
            }
        }
        Assert.assertEquals("Interface was not detected", implemented, actual);
    }
}
