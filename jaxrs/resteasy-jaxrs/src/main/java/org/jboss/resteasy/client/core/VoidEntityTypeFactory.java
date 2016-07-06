package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.EntityTypeFactory;

import javax.ws.rs.core.MultivaluedMap;

/**
* 
* @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
* 
* @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
*/
@Deprecated
public class VoidEntityTypeFactory implements EntityTypeFactory
{

   @SuppressWarnings("unchecked")
   public Class getEntityType(int status,
                              MultivaluedMap<String, Object> metadata)
   {
      return Void.class;
   }

}
