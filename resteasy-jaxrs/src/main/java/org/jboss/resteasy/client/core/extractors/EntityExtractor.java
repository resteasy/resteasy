package org.jboss.resteasy.client.core.extractors;


/**
 * EntityExtractor extract objects from responses. An extractor can extract a
 * status, a header, a cookie, the response body, the clientRequest object, the
 * clientResponse object, or anything else that a "response object" might need.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * @see EntityExtractorFactory
 */
public interface EntityExtractor<T>
{
   T extractEntity(ClientRequestContext context, Object... args);
}
