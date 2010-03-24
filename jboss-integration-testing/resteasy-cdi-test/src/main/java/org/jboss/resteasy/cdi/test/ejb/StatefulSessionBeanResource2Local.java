package org.jboss.resteasy.cdi.test.ejb;

import javax.ejb.Local;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.cdi.test.Cat;
import org.jboss.resteasy.cdi.test.Dog;
import org.jboss.resteasy.cdi.test.SubResource;

@Local
public interface StatefulSessionBeanResource2Local
{
   void init(Cat cat);
   boolean fieldInjection();
   boolean ejbFieldInjection();
   boolean jaxrsFieldInjection();
   String jaxrsFieldInjection2();
   boolean jaxrsSetterInjection();
   boolean constructorInjection();
   boolean initializerInjection();
   String jaxrsMethodInjection(String query);
   int getId();
   Dog testProviders();
   void setSetterUriInfo(UriInfo setterUriInfo);
   SubResource subResource();
   void remove();
}
