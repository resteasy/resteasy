package org.resteasy;

import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.spi.HttpOutput;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ParameterMarshaller
{
   void marshall(Object object, UriBuilderImpl uri, HttpOutput output);
}