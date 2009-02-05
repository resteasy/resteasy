package org.jboss.resteasy.client;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientExecutor
{
   ClientResponse execute(String verb, ClientRequest request) throws Exception;
}
