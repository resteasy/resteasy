package org.resteasy.spi;

/**
 * Factory that creates or finds a target resource to invoke on.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceFactory
{
   Object createResource(HttpRequest input, HttpResponse response);
}
