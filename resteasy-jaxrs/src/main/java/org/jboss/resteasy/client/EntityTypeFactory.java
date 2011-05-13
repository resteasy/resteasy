package org.jboss.resteasy.client;

import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

public interface EntityTypeFactory
{
   @SuppressWarnings("unchecked")
   Class getEntityType(int status, MultivaluedMap<String, Object> metadata);
}
