package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.*;

public class Regression636Test extends BaseResourceTest
{

   public static class BaseResources
   {

   }

   public static class BaseResource
   {

      protected String name;
      protected String description;
      protected String id;

      /**
       * Gets the value of the name property.
       *
       * @return possible object is {@link String }
       */
      public String getName()
      {
         return name;
      }

      /**
       * Sets the value of the name property.
       *
       * @param value allowed object is {@link String }
       */
      public void setName(String value)
      {
         this.name = value;
      }

      public boolean isSetName()
      {
         return (this.name != null);
      }

      /**
       * Gets the value of the description property.
       *
       * @return possible object is {@link String }
       */
      public String getDescription()
      {
         return description;
      }

      /**
       * Sets the value of the description property.
       *
       * @param value allowed object is {@link String }
       */
      public void setDescription(String value)
      {
         this.description = value;
      }

      public boolean isSetDescription()
      {
         return (this.description != null);
      }
   }

   @XmlRootElement
   public static class DataCenter extends BaseResource
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   public static class DataCenters
           extends BaseResources
   {

   }

   @Produces({MediaType.APPLICATION_XML})
   public interface UpdatableResource<R extends BaseResource>
   {

      @GET
      public R get();

      @PUT
      @Consumes({MediaType.APPLICATION_XML})
      public R update(R resource);
   }

   public static class AssignedPermissionsResource
   {
      @GET
      String hello()
      {
         return "hello";
      }
   }

   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public interface DataCenterResource extends UpdatableResource<DataCenter>
   {

      @Path("permissions")
      public AssignedPermissionsResource getPermissionsResource();

      // TODO These two methods should not have to be added as the annotation information is in a super generic interface

      /*
      @GET
      public DataCenter get();

      @PUT
      @Consumes({MediaType.APPLICATION_XML})
      public DataCenter update(DataCenter resource);
      */

   }

   @Path("/datacenters")
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public interface DataCentersResource
   {

      @GET
      @Formatted
      public DataCenters list();

      @POST
      @Formatted
      @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
      public Response add(DataCenter dataCenter);

      @DELETE
      @Path("{id}")
      public Response remove(@PathParam("id") String id);

      @DELETE
      @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
      @Path("{id}")
      public Response remove(@PathParam("id") String id, Action action);

      /**
       * Sub-resource locator method, returns individual DataCenterResource on which the
       * remainder of the URI is dispatched.
       *
       * @param id the DataCenter ID
       * @return matching subresource if found
       */
      @Path("{id}")
      public DataCenterResource getDataCenterSubResource(@PathParam("id") String id);
   }

   public static class AbstractBackendSubResource<R extends BaseResource, Q /* extends IVdcQueryable */> extends
           AbstractBackendResource<R, Q>
   {

   }

   public static class AbstractBackendResource<R extends BaseResource, Q /* extends IVdcQueryable */>
           extends BackendResource
   {

   }

   public static abstract class AbstractBackendCollectionResource<R extends BaseResource, Q /* extends IVdcQueryable */>
           extends AbstractBackendResource<R, Q>
   {

   }

   public static class BackendResource extends BaseBackendResource
   {

   }

   public static class BaseBackendResource
   {

   }

   public static class Guid implements Serializable
   {

   }

   public static class storage_pool extends IVdcQueryable implements INotifyPropertyChanged, BusinessEntity<Guid>
   {

      @Override
      public Guid getId()
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public void setId(Guid id)
      {
         // TODO Auto-generated method stub

      }
   }

   public interface INotifyPropertyChanged
   {

   }

   public static class IVdcQueryable
   {

   }

   public interface BusinessEntity<T extends Serializable> extends Serializable
   {

      /**
       * Returns the unique ID of the business entity.
       *
       * @return The unique ID of the business entity.
       */
      public T getId();

      /**
       * Sets the unique ID of the business entity
       *
       * @param id The unique ID of the business entity.
       */
      public void setId(T id);
   }

   public static class Action
           extends BaseResources
   {

   }

   public static class BackendDataCentersResource extends
           AbstractBackendCollectionResource<DataCenter, storage_pool> implements DataCentersResource
   {

      @Override
      public DataCenters list()
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Response add(DataCenter dataCenter)
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public Response remove(String id, Action action)
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public DataCenterResource getDataCenterSubResource(String id)
      {
         return new BackendDataCenterResource(id, this);
      }

      @Override
      public Response remove(String id)
      {
         // TODO Auto-generated method stub
         return null;
      }
   }

   public static class BackendDataCenterResource extends AbstractBackendSubResource<DataCenter, storage_pool>
           implements DataCenterResource
   {
      public BackendDataCenterResource(String id, BackendDataCentersResource backendDataCentersResource)
      {
         // TODO Auto-generated constructor stub
      }

      @Override
      public AssignedPermissionsResource getPermissionsResource()
      {
         return null;
      }

      @Override
      public DataCenter get()
      {
         DataCenter dc = new DataCenter();
         dc.setName("Bill");
         return dc;
      }

      @Override
      public DataCenter update(DataCenter resource)
      {
         return resource;
      }

//        @Override
//        public AttachedStorageDomainsResource getAttachedStorageDomainsResource() {
//            // TODO Auto-generated method stub
//            return null;
//        }
   }

   @Path("/")
   public static class Top
   {
      @Path("datacenters")
      public BackendDataCentersResource getDatacenters()
      {
         return new BackendDataCentersResource();
      }

      // here we get BackendDataCentersResource collection sub-resource BackendDataCenterResource
      // e.g "/datacenters/xxx"

      // and invoke one of the methods inherited from UpdatableResource on it
      // e.g get()/update()
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Top.class);
   }

   @Test
   public void testGetImplementationReflection() throws Exception
   {
      Class updatableResource = BackendDataCenterResource.class.getInterfaces()[0].getInterfaces()[0];
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
      ResteasyDeployment dep = deployment;
      ClientRequest request = new ClientRequest(generateURL("/datacenters/1"));
      ClientResponse res = request.get();
      Assert.assertEquals(200, res.getStatus());
      DataCenter dc = (DataCenter) res.getEntity(DataCenter.class);
      Assert.assertEquals(dc.getName(), "Bill");
      request = new ClientRequest(generateURL("/datacenters/1"));

      res = request.body("application/xml", dc).put();
      Assert.assertEquals(200, res.getStatus());
      dc = (DataCenter) res.getEntity(DataCenter.class);
      Assert.assertEquals(dc.getName(), "Bill");


   }
}
