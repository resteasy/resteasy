package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;


/**
 * EntityExtractor extract objects from responses. An extractor can extract a
 * status, a header, a cookie, the response body, the clientRequest object, the
 * clientResponse object, or anything else that a "response object" might need.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractorFactory
 */
public interface EntityExtractor<T>
{
   T extractEntity(ClientContext context, Object... args);
}
