package org.resteasy;

import org.resteasy.spi.HttpRequest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ParameterExtractor
{
   Object extract(HttpRequest request);
}
