package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Marshaller
{
   void marshall(Object object, UriBuilderImpl uri, ClientHttpOutput output);
}