package org.jboss.resteasy.test.providers.jaxb.regression;
import static org.jboss.resteasy.test.TestPortProvider.*;

import java.lang.reflect.Method;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.AbstractBackendCollectionResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.AbstractBackendResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.AbstractBackendSubResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.Action;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.AssignedPermissionsResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BackendDataCenterResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BackendDataCentersResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BackendResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BaseBackendResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BaseResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BaseResources;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.BusinessEntity;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.DataCenter;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.DataCenterResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.DataCenters;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.DataCentersResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.Guid;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.INotifyPropertyChanged;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.IVdcQueryable;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.Top;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.UpdatableResource;
import org.jboss.resteasy.test.providers.jaxb.regression.Regression636Classes.storage_pool;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Regression636Test extends BaseResourceTest
{

   @Override
   @Before
   public void before() throws Exception
   {
      addPerRequestResource(Top.class,
          AbstractBackendCollectionResource.class, AbstractBackendResource.class,
          AbstractBackendSubResource.class, Action.class, AssignedPermissionsResource.class,
          BackendDataCenterResource.class, BackendDataCentersResource.class,
          BackendResource.class, BaseBackendResource.class,
          BaseResource.class, BaseResources.class, BusinessEntity.class,
          DataCenter.class, DataCenters.class,
          DataCenterResource.class, DataCentersResource.class,
          Guid.class, IVdcQueryable.class, INotifyPropertyChanged.class,
          storage_pool.class, UpdatableResource.class, Regression636Classes.class

      );
      super.before();
   }

   @Test
   public void testGetImplementationReflection() throws Exception
   {
      Class<?> updatableResource = BackendDataCenterResource.class.getInterfaces()[0].getInterfaces()[0];
      Assert.assertEquals(updatableResource, UpdatableResource.class);
      Method update = null;
      for (Method method : updatableResource.getMethods())
      {
         if (method.getName().equals("update")) update = method;
      }
      Assert.assertNotNull(update);

      Method implemented = Types.getImplementingMethod(BackendDataCenterResource.class, update);

      Method actual = null;
      for (Method method : BackendDataCenterResource.class.getMethods())
      {
         if (method.getName().equals("update") && !method.isSynthetic()) actual = method;
      }

      Assert.assertEquals(implemented, actual);

   }

   @Test
   public void testInheritance() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/datacenters/1"));
      ClientResponse<?> res = request.get();
      Assert.assertEquals(200, res.getStatus());
      DataCenter dc = res.getEntity(DataCenter.class);
      Assert.assertEquals(dc.getName(), "Bill");
      request = new ClientRequest(generateURL("/datacenters/1"));

      res = request.body("application/xml", dc).put();
      Assert.assertEquals(200, res.getStatus());
      dc = res.getEntity(DataCenter.class);
      Assert.assertEquals(dc.getName(), "Bill");


   }
}
