package org.jboss.resteasy.plugins.touri;

import org.jboss.resteasy.spi.touri.URIResolver;
import org.jboss.resteasy.spi.touri.URIable;

/**
 * URIResolver Adapter for URIable classes
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public class URIableURIResolver implements URIResolver
{
   public boolean handles(Class<?> type)
   {
      return URIable.class.isAssignableFrom(type);
   }

   public String resolveURI(Object object)
   {
      return ((URIable) object).toURI();
   }

}
