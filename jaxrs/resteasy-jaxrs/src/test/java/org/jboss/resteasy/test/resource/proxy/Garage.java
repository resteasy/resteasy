package org.jboss.resteasy.test.resource.proxy;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/garage")
@Produces("text/plain")
public class Garage
{
   @Path("/car")
   public Car getCar()
   {
      return lookupSeamComponent();
   }

   /**
    * This method simulates obtaining a bean from Seam and returning it. - Components.getInstance(Car.class)
    * Seam is not used in this test. Car instance is wrapped with a javassist proxy.
    *
    * @return Proxied Car instance
    */
   private Car lookupSeamComponent()
   {
      Car car = new Car("MT-123AB");
      CarProxy interceptor = new CarProxy(car);

      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(Car.class);

      ProxyObject component = null;
      try
      {
         component = (ProxyObject) factory.createClass().newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

      component.setHandler(interceptor);

      return (Car) component;
   }
}
