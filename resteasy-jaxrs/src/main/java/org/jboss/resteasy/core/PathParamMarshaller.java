package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamMarshaller implements Marshaller
{
   private String paramName;
   private boolean encoded;

   public PathParamMarshaller(String paramName, boolean encoded)
   {
      this.paramName = paramName;
      this.encoded = encoded;
   }

   public void marshall(Object object, UriBuilderImpl uri, ClientHttpOutput output)
   {
      uri.substitutePathParam(paramName, object, encoded);
   }
}
