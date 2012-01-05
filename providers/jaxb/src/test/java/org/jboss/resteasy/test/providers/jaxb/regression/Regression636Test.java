package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Regression636Test extends BaseResourceTest
{
   @XmlRootElement
   public static class DataCenter
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

   @Produces({MediaType.APPLICATION_XML})
   public interface UpdatableResource<R>
   {

      @GET
      public R get();

      @PUT
      @Consumes({MediaType.APPLICATION_XML})
      public R update(R resource);
   }

   public class AssignedPermissionsResource
   {
      @GET
      String hello()
      {
         return "hello";
      }
   }

   @Produces({MediaType.APPLICATION_XML})
   public interface DataCenterResource extends UpdatableResource<DataCenter>
   {
      @Path("permissions")
      public AssignedPermissionsResource getPermissionsResource();
   }

   public static class AbstractBackendSubResource
   {

   }

   public static class BackendDataCenterResource extends AbstractBackendSubResource implements DataCenterResource
   {
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
   }

   @Path("/")
   public static class Top
   {
      @Path("datacenter")
      public BackendDataCenterResource getDatacenter()
      {
         return new BackendDataCenterResource();
      }
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      addPerRequestResource(Top.class);
   }

   @Test
   public void testInheritance() throws Exception
   {
      ResteasyDeployment dep = deployment;
      ClientRequest request = new ClientRequest(generateURL("/datacenter"));
      ClientResponse res = request.get();
      Assert.assertEquals(200, res.getStatus());

   }

}
