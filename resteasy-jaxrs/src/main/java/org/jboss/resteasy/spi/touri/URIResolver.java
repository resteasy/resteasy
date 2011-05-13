package org.jboss.resteasy.spi.touri;

/**
 * This is the interface that defines all object to uri transformations in
 * ObjectToURI
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public interface URIResolver
{
   public boolean handles(Class<?> type);

   public String resolveURI(Object object);
}
