package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Can inject lists.
 */
public class ListFormInjector extends AbstractCollectionFormInjector<List>
{

   /**
    * Constructor.
    */
   public ListFormInjector(Class collectionType, Class genericType, String prefix, ResteasyProviderFactory factory)
   {
      super(collectionType, genericType, prefix, Pattern.compile("^" + prefix + "\\[(\\d+)\\]"), factory);
   }

   /**
    * {@inheritDoc}
    *
    * @return ArrayList
    */
   @Override
   protected List createInstance(Class collectionType)
   {
      return new ArrayList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void addTo(List collection, String key, Object value)
   {
      collection.add(Integer.parseInt(key), value);
   }
}