package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.EntityTypeFactory;

import javax.ws.rs.core.MultivaluedMap;

public class VoidEntityTypeFactory implements EntityTypeFactory
{

   @SuppressWarnings("unchecked")
   public Class getEntityType(int status,
                              MultivaluedMap<String, Object> metadata)
   {
      return Void.class;
   }

}
