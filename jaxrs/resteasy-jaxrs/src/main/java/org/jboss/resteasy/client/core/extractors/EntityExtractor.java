package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

/**
 * EntityExtractors extract objects from responses.  Different types of extractors
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @see EntityExtractorFactory
 */
public interface EntityExtractor<T>
{
   T extractEntity(ClientRequest request, BaseClientResponse<T> clientResponse);
}
