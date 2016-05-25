package org.jboss.resteasy.tests;

import javax.ws.rs.GET;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface SingletonLocalIF
{
   @GET
   String get();
}
