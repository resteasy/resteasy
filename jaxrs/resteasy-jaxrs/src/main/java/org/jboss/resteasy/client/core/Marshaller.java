package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Marshaller
{
   void buildUri(Object object, UriBuilderImpl uri);

   void setHeaders(Object object, HttpMethodBase httpMethod);

   void buildRequest(Object object, HttpMethodBase httpMethod);
}