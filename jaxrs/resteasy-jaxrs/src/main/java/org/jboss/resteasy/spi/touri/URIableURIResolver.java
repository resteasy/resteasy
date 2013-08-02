package org.jboss.resteasy.spi.touri;

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
