package org.jboss.resteasy.spi;

import javax.ws.rs.core.MediaType;

/**
 * Allows you to access the entity's raw bytes as well as the marshalled object.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MarshalledEntity<T>
{
   byte[] getMarshalledBytes();
   T getEntity();

}
